package mz.org.csaude.mentoring.view.tutored;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.color.MaterialColors;
import com.google.android.material.search.SearchBar;
import com.google.android.material.search.SearchView;

import java.util.ArrayList;

import mz.org.csaude.mentoring.R;
import mz.org.csaude.mentoring.base.activity.BaseActivity;
import mz.org.csaude.mentoring.base.viewModel.BaseViewModel;
import mz.org.csaude.mentoring.databinding.ActivityTutoredBinding;
import mz.org.csaude.mentoring.view.tutored.fragment.Searchable;
import mz.org.csaude.mentoring.viewmodel.tutored.TutoredStagesVM;

public class TutoredActivity extends BaseActivity {

    private ActivityTutoredBinding binding;
    private NavController navController;

    private ActivityResultLauncher<Intent> voiceLauncher;
    private ActivityResultLauncher<String> micPermissionLauncher;

    // Debounce opcional (deixe 0 para desligar)
    private final android.os.Handler searchHandler = new android.os.Handler(android.os.Looper.getMainLooper());
    private Runnable searchRunnable;
    private static final long SEARCH_DEBOUNCE_MS = 250L;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_tutored);
        binding.setViewModel(getRelatedViewModel());
        binding.setLifecycleOwner(this);

        // NavHost + BottomNavigation
        NavHostFragment host = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_tutored);
        if (host != null) {
            navController = host.getNavController();
            NavigationUI.setupWithNavController(binding.bottomNav, navController);
        }

        // Registradores de resultado
        registerVoiceLaunchers();

        // SearchBar + SearchView (Material 3)
        setupSearch(binding.searchBar, binding.searchView);

        // Se já houver query salva no VM, reflita no SearchBar e no editor
        String existing = getRelatedViewModel().getCurrentQuery();
        if (existing != null && !existing.isEmpty()) {
            binding.searchBar.setText(existing);
            EditText et = binding.searchView.getEditText();
            et.setText(existing);
            et.setSelection(existing.length());
        }
    }

    private void setupSearch(SearchBar searchBar, SearchView searchView) {
        // Vincula o SearchView ao SearchBar
        searchView.setupWithSearchBar(searchBar);

        // Abre ao tocar no SearchBar
        searchBar.setOnClickListener(v -> searchView.show());

        // Editor interno do SearchView
        final EditText et = searchView.getEditText();

        // Cores de texto visíveis em qualquer tema
        int onSurface = MaterialColors.getColor(searchView, com.google.android.material.R.attr.colorOnSurface);
        int onSurfaceVariant = MaterialColors.getColor(searchView, com.google.android.material.R.attr.colorOnSurfaceVariant);
        et.setTextColor(onSurface);
        et.setHint(getString(R.string.search));
        et.setHintTextColor(onSurfaceVariant);
        et.setAlpha(1f);
        et.setCursorVisible(true);

        // Foco + teclado quando o overlay abre
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

        // Mostrar/ocultar botão Clear conforme há texto
        final Runnable updateClearVisibility = () -> {
            if (searchBar.getMenu() != null) {
                var clear = searchBar.getMenu().findItem(R.id.action_clear);
                if (clear != null) {
                    boolean hasText = et.getText() != null && et.getText().length() > 0;
                    clear.setVisible(hasText);
                }
            }
        };

        // Enquanto digita: espelha no SearchBar + debounce + alterna "clear"
        et.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Espelha (isso garante que, ao fechar, o SearchBar mostre o texto)
                searchBar.setText(s);

                // Dispara busca com debounce
                if (searchRunnable != null) searchHandler.removeCallbacks(searchRunnable);
                final String q = s == null ? "" : s.toString();
                searchRunnable = () -> {
                    dispatchQuery(q);
                };
                searchHandler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_MS);

                updateClearVisibility.run();
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        // IME (Enter): confirma busca, mantém texto no SearchBar e fecha overlay
        et.setOnEditorActionListener((v, actionId, event) -> {
            String q = v.getText() == null ? "" : v.getText().toString();
            searchBar.setText(q);        // mantém visível no SearchBar
            dispatchQuery(q);            // envia aos fragments + salva no VM
            searchView.hide();           // fecha overlay
            return true;
        });

        // Menu: voice + clear
        searchBar.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.action_voice) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                        == PackageManager.PERMISSION_GRANTED) {
                    startVoiceInput();
                } else {
                    micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO);
                }
                return true;
            } else if (id == R.id.action_clear) {
                et.setText("");
                searchBar.setText("");
                dispatchQuery("");          // limpa resultado
                updateClearVisibility.run();
                return true;
            }
            return false;
        });

        // Mantém o estado do clear consistente nas transições
        searchView.addTransitionListener((sv, previous, state) -> sv.post(updateClearVisibility));

        // Estado inicial
        updateClearVisibility.run();
    }

    private void registerVoiceLaunchers() {
        // Resultado de fala
        voiceLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        ArrayList<String> matches = result.getData()
                                .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                        if (matches != null && !matches.isEmpty()) {
                            String spoken = matches.get(0);
                            // Mostra overlay e preenche texto
                            binding.searchView.show();
                            EditText et = binding.searchView.getEditText();
                            et.setText(spoken);
                            et.setSelection(spoken.length());
                            // Espelha e dispara busca (auto-submit)
                            binding.searchBar.setText(spoken);
                            binding.searchView.hide();
                            dispatchQuery(spoken);
                        }
                    }
                }
        );

        // Permissão do microfone (opcional)
        micPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) startVoiceInput();
                    else Toast.makeText(this, R.string.mic_permission_denied, Toast.LENGTH_SHORT).show();
                }
        );
    }

    private void startVoiceInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "pt-MZ"); // opcional
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.speak_now));
        try {
            voiceLauncher.launch(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, R.string.voice_search_not_available, Toast.LENGTH_LONG).show();
        }
    }

    /** Envia o texto ao fragmento visível (Searchable) e salva no VM compartilhado */
    private void dispatchQuery(String q) {
        String safe = q == null ? "" : q;

        // 1) Notifica o fragmento atual, se ele implementar Searchable
        Searchable target = findCurrentSearchable();
        if (target != null) target.onSearchQueryChanged(safe);

        // 2) Salva no VM compartilhado (para restaurar ao recriar tela)
        getRelatedViewModel().setCurrentQuery(safe);
    }

    /** Encontra o fragmento atual exibido pelo NavHost que implementa Searchable */
    private @Nullable Searchable findCurrentSearchable() {
        Fragment host = getSupportFragmentManager().findFragmentById(R.id.nav_host_tutored);
        if (host instanceof NavHostFragment) {
            for (Fragment child : ((NavHostFragment) host).getChildFragmentManager().getFragments()) {
                if (child instanceof Searchable && child.isVisible()) {
                    return (Searchable) child;
                }
            }
        }
        return null;
    }

    // ===== BaseActivity =====

    @Override
    public BaseViewModel initViewModel() {
        return new ViewModelProvider(this).get(TutoredStagesVM.class);
    }

    @Override
    public TutoredStagesVM getRelatedViewModel() {
        return (TutoredStagesVM) super.getRelatedViewModel();
    }

    @Override
    public boolean onSupportNavigateUp() {
        return (navController != null && navController.navigateUp()) || super.onSupportNavigateUp();
    }
}
