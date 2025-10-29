package mz.org.csaude.mentoring.adapter.recyclerview.ronda;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import mz.org.csaude.mentoring.R;
import mz.org.csaude.mentoring.adapter.recyclerview.generic.AbstractRecycleViewAdapter;
import mz.org.csaude.mentoring.base.activity.BaseActivity;
import mz.org.csaude.mentoring.databinding.RondaListItemBinding;
import mz.org.csaude.mentoring.model.ronda.Ronda;
import mz.org.csaude.mentoring.viewmodel.ronda.RondaSearchVM;

public class RondaAdapter extends AbstractRecycleViewAdapter<Ronda> {

    public RondaAdapter(RecyclerView recyclerView, List<Ronda> records, BaseActivity activity) {
        super(recyclerView, records, activity);
        setHasStableIds(false);
    }

    public Ronda getItemAt(int position) {
        if (position < 0 || position >= records.size()) return null;
        return records.get(position);
    }

    public void removeAt(int position) {
        if (position < 0 || position >= records.size()) return;
        records.remove(position);
        notifyItemRemoved(position);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RondaListItemBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.ronda_list_item,
                parent,
                false
        );
        return new RondaViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder rawHolder, int position) {
        RondaViewHolder holder = (RondaViewHolder) rawHolder;
        Ronda ronda = records.get(position);

        holder.binding.setRonda(ronda);
        holder.binding.setViewModel((RondaSearchVM) activity.getRelatedViewModel());
        holder.binding.executePendingBindings();

        RondaSearchVM vm = (RondaSearchVM) activity.getRelatedViewModel();

        // ===== Botões do fundo revelados pelo swipe =====
        holder.binding.bgLeftBtn.setOnClickListener(v -> {
            // Fecha visualmente
            holder.binding.viewForeground.animate().translationX(0f).setDuration(150)
                    .withEndAction(() -> holder.binding.viewForeground.setTag(R.id.tag_open_state, 0))
                    .start();
            // Ação: VM pede confirmação/valida
            vm.delete(ronda);
        });

        holder.binding.bgRightBtn.setOnClickListener(v -> {
            holder.binding.viewForeground.animate().translationX(0f).setDuration(150)
                    .withEndAction(() -> holder.binding.viewForeground.setTag(R.id.tag_open_state, 0))
                    .start();
            vm.edit(ronda);
        });

        // ===== Click no item: fecha se aberto; senão abre detalhes =====
        holder.binding.viewForeground.setOnClickListener(v -> {
            Object tag = v.getTag(R.id.tag_open_state);
            int state = (tag instanceof Integer) ? (Integer) tag : 0;
            if (state != 0) {
                v.animate().translationX(0f).setDuration(150)
                        .withEndAction(() -> v.setTag(R.id.tag_open_state, 0)).start();
            } else {
                vm.goToMentoriships(ronda);
            }
        });

        // ===== Botão more (⋮) =====
        holder.binding.btnMore.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(v.getContext(), holder.binding.btnMore);
            popup.getMenuInflater().inflate(R.menu.menu_ronda_item_overflow, popup.getMenu());

            boolean closed = safeIsClosed(ronda);
            boolean rondaZero = safeIsRondaZero(ronda);
            popup.getMenu().findItem(R.id.action_print).setVisible(closed && !rondaZero);
            popup.getMenu().findItem(R.id.action_edit).setVisible(!closed);
            popup.getMenu().findItem(R.id.action_delete).setVisible(!closed);

            popup.setOnMenuItemClickListener(item -> handleOverflowClick(item, ronda));
            popup.show();
        });
    }

    private boolean handleOverflowClick(@NonNull MenuItem item, @NonNull Ronda ronda) {
        RondaSearchVM vm = (RondaSearchVM) activity.getRelatedViewModel();
        int id = item.getItemId();
        if (id == R.id.action_open) {
            vm.goToMentoriships(ronda);
            return true;
        } else if (id == R.id.action_print) {
            vm.printRondaSummary(ronda);
            return true;
        } else if (id == R.id.action_edit) {
            vm.edit(ronda);
            return true;
        } else if (id == R.id.action_delete) {
            vm.delete(ronda);
            return true;
        }
        return false;
    }

    private boolean safeIsClosed(Ronda r) {
        try { return r.isClosed(); } catch (Throwable ignored) {
            try { return (boolean) Ronda.class.getField("closed").get(r); } catch (Throwable ignored2) {
                return false;
            }
        }
    }

    private boolean safeIsRondaZero(Ronda r) {
        try { return r.isRondaZero(); } catch (Throwable ignored) {
            try { return (boolean) Ronda.class.getField("rondaZero").get(r); } catch (Throwable ignored2) {
                return false;
            }
        }
    }

    public static class RondaViewHolder extends RecyclerView.ViewHolder {
        final RondaListItemBinding binding;
        public RondaViewHolder(@NonNull RondaListItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
