package mz.org.csaude.mentoring.view.ronda;

import android.Manifest;
import android.animation.TimeInterpolator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.color.MaterialColors;
import com.google.android.material.search.SearchBar;
import com.google.android.material.search.SearchView;

import mz.org.csaude.mentoring.R;
import mz.org.csaude.mentoring.adapter.recyclerview.ronda.RondaAdapter;
import mz.org.csaude.mentoring.base.activity.BaseActivity;
import mz.org.csaude.mentoring.base.viewModel.BaseViewModel;
import mz.org.csaude.mentoring.databinding.ActivityMentoringCycleListBinding;
import mz.org.csaude.mentoring.model.rondatype.RondaType;
import mz.org.csaude.mentoring.model.rondatype.RondaTypeCode;
import mz.org.csaude.mentoring.util.Utilities;
import mz.org.csaude.mentoring.view.common.VerticalSpaceItemDecoration;
import mz.org.csaude.mentoring.viewmodel.ronda.RondaSearchVM;

public class RondaActivity extends BaseActivity {

    private ActivityMentoringCycleListBinding mentoringCycleListBinding;
    private RecyclerView rondasRecyclerView;
    private RondaAdapter rondaAdapter;
    private boolean suppressSearch = false;

    // Debounce
    private final android.os.Handler searchHandler = new android.os.Handler(android.os.Looper.getMainLooper());
    private Runnable searchRunnable;
    private static final long SEARCH_DEBOUNCE_MS = 250L;

    // Swipe latch
    private static final float REVEAL_RATIO = 0.40f;
    private static final float SNAP_RATIO   = 0.15f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        mentoringCycleListBinding = DataBindingUtil.setContentView(this, R.layout.activity_mentoring_cycle_list);
        mentoringCycleListBinding.setLifecycleOwner(this);
        mentoringCycleListBinding.setViewModel(getRelatedViewModel());

        // Status bar com ?attr/colorPrimary e ícones brancos
        final var root = mentoringCycleListBinding.getRoot();
        int colorPrimary = MaterialColors.getColor(root, androidx.preference.R.attr.colorPrimary);
        getWindow().setStatusBarColor(colorPrimary);
        new WindowInsetsControllerCompat(getWindow(), root).setAppearanceLightStatusBars(false);

        // Intent extras
        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null) {
            getRelatedViewModel().setTitle((String) intent.getExtras().get("title"));
            getRelatedViewModel().setRondaType((RondaType) intent.getExtras().get("rondaType"));
        }

        // Recycler
        rondasRecyclerView = mentoringCycleListBinding.rcvRondas;

        // Bottom nav
        var bottomNav = mentoringCycleListBinding.bottomNav;
        selectBottomItemFor(getRelatedViewModel().getRondaTypeCode(), bottomNav);
        bottomNav.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_zero:
                    getRelatedViewModel().setRondaType(RondaTypeCode.ZERO);
                    break;
                case R.id.nav_ciclo:
                    getRelatedViewModel().setRondaType(RondaTypeCode.MENTORIA_INTERNA);
                    break;
                case R.id.nav_semestral:
                    getRelatedViewModel().setRondaType(RondaTypeCode.SEMESTRAL);
                    break;
                default:
                    return false;
            }
            getRelatedViewModel().initSearch();
            return true;
        });

        // Search
        setupSearch(mentoringCycleListBinding.searchBar, mentoringCycleListBinding.searchView);

        // Restaura query pré-existente (mantém texto no SearchBar e no editor)
        String existing = getRelatedViewModel().getQuery();
        if (existing != null && !existing.isEmpty()) {
            mentoringCycleListBinding.searchBar.setText(existing);
            mentoringCycleListBinding.searchView.getEditText().setText(existing);
            mentoringCycleListBinding.searchView.getEditText().setSelection(existing.length());
        }

        // Load inicial
        getRelatedViewModel().initSearch();
    }

    private void setupSearch(SearchBar searchBar, SearchView searchView) {
        searchView.setupWithSearchBar(searchBar);
        searchBar.setOnClickListener(v -> searchView.show());

        EditText et = searchView.getEditText();

        // Focus + keyboard when overlay opens
        searchView.addTransitionListener((sv, oldState, newState) -> {
            if (newState == SearchView.TransitionState.SHOWN) {
                et.post(() -> {
                    et.requestFocus();
                    var imm = (android.view.inputmethod.InputMethodManager)
                            getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
                    if (imm != null) imm.showSoftInput(et, android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT);
                });
            }
        });

        int onSurface = MaterialColors.getColor(searchView, com.google.android.material.R.attr.colorOnSurface);
        int onSurfaceVariant = MaterialColors.getColor(searchView, com.google.android.material.R.attr.colorOnSurfaceVariant);
        et.setTextColor(onSurface);
        et.setHint(getString(R.string.search));
        et.setHintTextColor(onSurfaceVariant);
        et.setAlpha(1f);
        et.setCursorVisible(true);

        final Runnable updateClearVisibility = () -> {
            if (searchBar.getMenu() != null) {
                var clearItem = searchBar.getMenu().findItem(R.id.action_clear);
                if (clearItem != null) {
                    boolean hasText = et.getText() != null && et.getText().length() > 0;
                    clearItem.setVisible(hasText);
                }
            }
        };

        // Mirror text + debounce search (but skip when we are changing text programmatically)
        et.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchBar.setText(s); // keep searchBar text visible

                if (suppressSearch) { // prevents duplicate calls on programmatic setText
                    updateClearVisibility.run();
                    return;
                }

                if (searchRunnable != null) searchHandler.removeCallbacks(searchRunnable);
                final String q = s == null ? "" : s.toString();
                searchRunnable = () -> {
                    // only hit VM when value actually changed (extra safety)
                    if (!q.equals(getRelatedViewModel().getQuery())) {
                        getRelatedViewModel().setQuery(q);
                        getRelatedViewModel().initSearch();
                    }
                };
                searchHandler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_MS);
                updateClearVisibility.run();
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        // IME confirm: one single call
        et.setOnEditorActionListener((v, actionId, event) -> {
            String q = v.getText() == null ? "" : v.getText().toString();
            searchBar.setText(q);
            if (!q.equals(getRelatedViewModel().getQuery())) {
                getRelatedViewModel().setQuery(q);
                getRelatedViewModel().initSearch();
            }
            searchView.hide();
            return true;
        });

        // Clear: mute watcher while changing text, then fire exactly one search
        searchBar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_clear) {
                if (searchRunnable != null) searchHandler.removeCallbacks(searchRunnable);

                suppressSearch = true;
                et.setText("");
                searchBar.setText("");
                suppressSearch = false;

                // single search call
                if (!"".equals(getRelatedViewModel().getQuery())) {
                    getRelatedViewModel().setQuery("");
                    getRelatedViewModel().initSearch();
                }

                updateClearVisibility.run();
                return true;
            }
            return false;
        });

        searchView.addTransitionListener((sv, previousState, newState) -> sv.post(updateClearVisibility));
        updateClearVisibility.run();
    }


    private void selectBottomItemFor(RondaTypeCode code, com.google.android.material.bottomnavigation.BottomNavigationView bottomNav) {
        int id = R.id.nav_ciclo;
        if (code == RondaTypeCode.ZERO) id = R.id.nav_zero;
        else if (code == RondaTypeCode.SEMESTRAL) id = R.id.nav_semestral;
        bottomNav.setSelectedItemId(id);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getRelatedViewModel().initSearch();
    }

    @Override
    public BaseViewModel initViewModel() {
        return new ViewModelProvider(this).get(RondaSearchVM.class);
    }

    @Override
    public RondaSearchVM getRelatedViewModel() {
        return (RondaSearchVM) super.getRelatedViewModel();
    }

    public void populateRecyclerView() {
        rondasRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        if (rondasRecyclerView.getItemDecorationCount() == 0) {
            int space = getResources().getDimensionPixelSize(R.dimen.dimen_8dp);
            rondasRecyclerView.addItemDecoration(new VerticalSpaceItemDecoration(space, true));
        }

        rondaAdapter = new RondaAdapter(rondasRecyclerView, getRelatedViewModel().getSearchResults(), this);
        rondasRecyclerView.setAdapter(rondaAdapter);
        rondasRecyclerView.scheduleLayoutAnimation();

        rondasRecyclerView.clearOnScrollListeners();
        rondasRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override public void onScrollStateChanged(@NonNull RecyclerView rv, int newState) {
                if (newState != RecyclerView.SCROLL_STATE_IDLE) closeAnyOpenRow();
            }
        });

        new androidx.recyclerview.widget.ItemTouchHelper(
                new androidx.recyclerview.widget.ItemTouchHelper.SimpleCallback(
                        0, androidx.recyclerview.widget.ItemTouchHelper.LEFT | androidx.recyclerview.widget.ItemTouchHelper.RIGHT) {

                    private static final long SETTLE_DURATION = 180L;
                    private static final long CLOSE_OTHERS_DURATION = 140L;
                    private final TimeInterpolator SETTLE_INTERP =
                            new android.view.animation.OvershootInterpolator(0.85f);
                    private final TimeInterpolator DRAG_INTERP =
                            new android.view.animation.DecelerateInterpolator();

                    @Override public boolean onMove(@NonNull RecyclerView rv,
                                                    @NonNull RecyclerView.ViewHolder vh,
                                                    @NonNull RecyclerView.ViewHolder target) { return false; }

                    @Override public void onSwiped(@NonNull RecyclerView.ViewHolder vh, int dir) { /* no-op */ }

                    @Override
                    public void clearView(@NonNull RecyclerView rv, @NonNull RecyclerView.ViewHolder vh) {
                        super.clearView(rv, vh);
                        android.view.View fg = vh.itemView.findViewById(R.id.view_foreground);
                        if (fg != null) {
                            fg.animate().cancel();
                            fg.setScaleX(1f);
                            fg.setScaleY(1f);
                            fg.setAlpha(1f);
                            float tx = fg.getTranslationX();
                            androidx.core.view.ViewCompat.setElevation(fg, Math.abs(tx) > 0.5f ? 6f : 0f);
                        }
                    }

                    @Override
                    public void onChildDraw(@NonNull android.graphics.Canvas c,
                                            @NonNull RecyclerView rv,
                                            @NonNull RecyclerView.ViewHolder vh,
                                            float dX, float dY,
                                            int actionState, boolean isCurrentlyActive) {

                        android.view.View itemView   = vh.itemView;
                        android.view.View foreground = itemView.findViewById(R.id.view_foreground);
                        if (foreground == null) {
                            super.onChildDraw(c, rv, vh, dX, dY, actionState, isCurrentlyActive);
                            return;
                        }

                        Object tag = foreground.getTag(R.id.tag_open_state);
                        int openState = (tag instanceof Integer) ? (Integer) tag : 0;

                        float width   = itemView.getWidth();
                        float maxDx   = width * REVEAL_RATIO;
                        float minSnap = width * SNAP_RATIO;

                        float clampedDx = Math.max(-maxDx, Math.min(dX, maxDx));
                        float progress = Math.min(1f, Math.abs(clampedDx) / maxDx);

                        if (isCurrentlyActive) {
                            foreground.animate().cancel();
                            foreground.setTranslationX(clampedDx);
                            float squeeze = 1f - 0.02f * progress;
                            foreground.setScaleX(squeeze);
                            foreground.setScaleY(squeeze);
                            foreground.setAlpha(1f - 0.05f * progress);
                            androidx.core.view.ViewCompat.setElevation(foreground, 0f);
                        } else {
                            float targetDx;
                            if (Math.abs(dX) >= minSnap) {
                                openState = dX > 0 ? +1 : -1;
                            }
                            if (openState == +1)      targetDx = +maxDx;
                            else if (openState == -1) targetDx = -maxDx;
                            else                      targetDx = 0f;

                            foreground.setTag(R.id.tag_open_state, openState);

                            if (openState != 0) {
                                for (int i = 0; i < rv.getChildCount(); i++) {
                                    android.view.View child = rv.getChildAt(i);
                                    if (child == itemView) continue;
                                    android.view.View otherFg = child.findViewById(R.id.view_foreground);
                                    if (otherFg != null) {
                                        Object ot = otherFg.getTag(R.id.tag_open_state);
                                        if (ot instanceof Integer && ((Integer) ot) != 0) {
                                            otherFg.animate()
                                                    .translationX(0f)
                                                    .setDuration(CLOSE_OTHERS_DURATION)
                                                    .setInterpolator(DRAG_INTERP)
                                                    .withEndAction(() -> otherFg.setTag(R.id.tag_open_state, 0))
                                                    .start();
                                            androidx.core.view.ViewCompat.setElevation(otherFg, 0f);
                                        }
                                    }
                                }
                            }

                            foreground.animate()
                                    .translationX(targetDx)
                                    .setDuration(SETTLE_DURATION)
                                    .setInterpolator(SETTLE_INTERP)
                                    .withStartAction(() -> {
                                        foreground.setScaleX(1f);
                                        foreground.setScaleY(1f);
                                        foreground.setAlpha(1f);
                                    })
                                    .withEndAction(() ->
                                            androidx.core.view.ViewCompat.setElevation(foreground,
                                                    Math.abs(targetDx) > 0.5f ? 6f : 0f))
                                    .start();
                        }

                        super.onChildDraw(c, rv, vh, 0f, 0f, actionState, isCurrentlyActive);
                    }

                    @Override public float getSwipeThreshold(@NonNull RecyclerView.ViewHolder viewHolder) { return 2.0f; }
                    @Override public float getSwipeEscapeVelocity(float defaultValue) { return Float.MAX_VALUE; }
                    @Override public float getSwipeVelocityThreshold(float defaultValue) { return Float.MAX_VALUE; }
                }
        ).attachToRecyclerView(rondasRecyclerView);
    }

    private boolean closeAnyOpenRow() {
        for (int i = 0; i < rondasRecyclerView.getChildCount(); i++) {
            android.view.View child = rondasRecyclerView.getChildAt(i);
            android.view.View fg = child.findViewById(R.id.view_foreground);
            if (fg != null) {
                Object t = fg.getTag(R.id.tag_open_state);
                if (t instanceof Integer && ((Integer) t) != 0) {
                    fg.animate().translationX(0f).setDuration(120)
                            .withEndAction(() -> fg.setTag(R.id.tag_open_state, 0)).start();
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if (closeAnyOpenRow()) return;
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // ===== permissões/print =====
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_WRITE_STORAGE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getRelatedViewModel().printRondaReport();
        } else {
            Utilities.displayAlertDialog(this, getString(R.string.permission_print_error)).show();
        }
    }

    public void checkStoragePermission() {
        boolean hasPermission = (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermission) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Utilities.displayAlertDialog(this, getString(R.string.permission_required_message),
                        () -> ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_STORAGE)
                ).show();
            } else {
                Utilities.displayAlertDialog(this, getString(R.string.permission_denied_forever_message),
                        () -> {
                            Intent i = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                    android.net.Uri.parse("package:" + getPackageName()));
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i);
                        }
                ).show();
            }
        } else {
            getRelatedViewModel().printRondaReport();
        }
    }
}
