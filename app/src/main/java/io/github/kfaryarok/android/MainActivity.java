package io.github.kfaryarok.android;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.kfaryarok.android.updates.UpdateAdapter;
import io.github.kfaryarok.android.updates.UpdateHelper;
import io.github.kfaryarok.android.updates.api.Update;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.internal.functions.Functions;

public class MainActivity extends AppCompatActivity implements UpdateAdapter.UpdateAdapterOnClickHandler {

    @BindView(R.id.srl_main)
    public SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.rv_updates)
    public RecyclerView recyclerViewUpdates;
    public UpdateAdapter adapterRecyclerView;

    @BindView(R.id.tv_main_info)
    public TextView infoTextView;

    @BindView(R.id.tv_main_warning)
    public TextView warningTextView;

    private Toast toast;

    private Consumer<? super Update> nextConsumerAddToAdapter = adapterRecyclerView::addUpdate;
    private Action completeConsumerStopRefresh = () -> swipeRefreshLayout.setRefreshing(false);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        setupLayoutDirection();
        setupRecyclerView();

        swipeRefreshLayout.setOnRefreshListener(() -> {
            adapterRecyclerView.updates.clear();
            swipeRefreshLayout.setRefreshing(true);
            UpdateHelper.getUpdatesReactively(this, nextConsumerAddToAdapter, Functions.emptyConsumer(),
                    completeConsumerStopRefresh, Functions.emptyConsumer());
        });
    }

    /**
     * Changes the layout direction of the activity to LTR, to ensure direction of the UI
     * stays the same regardless of the phone's language
     */
    public void setupLayoutDirection() {
        ViewCompat.setLayoutDirection(getWindow().getDecorView(), ViewCompat.LAYOUT_DIRECTION_LTR);
    }

    private void setupRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerViewUpdates.setLayoutManager(layoutManager);
        recyclerViewUpdates.setHasFixedSize(true);

        adapterRecyclerView = new UpdateAdapter(this);
        swipeRefreshLayout.setRefreshing(true);
        UpdateHelper.getUpdatesReactively(this, nextConsumerAddToAdapter, Functions.emptyConsumer(),
                completeConsumerStopRefresh, Functions.emptyConsumer());
        recyclerViewUpdates.setAdapter(adapterRecyclerView);
    }



    /**
     * When card is clicked, expand it if needed
     * @param v The card View object
     * @param update The update that the card displays
     */
    @Override
    public void onClickCard(View v, Update update) {
        // if card is clicked and the line count is bigger than 3 (meaning it can be expanded/"dexpanded")
        TextView tvText = v.findViewById(R.id.tv_updatecard_text);
        View viewExpand = v.findViewById(R.id.view_updatecard_expand);
        if (tvText.getLineCount() > 3) {
            // if current max lines is 3, expand to 100 lines, and else "dexpand" back to 3
            // it uses TextViewCompat instead of the given method for API 15 compatibility
            if (TextViewCompat.getMaxLines(tvText) == 3) {
                tvText.setMaxLines(100);
                viewExpand.setBackgroundResource(R.drawable.ic_arrow_drop_up_grey_600_24dp);
            } else {
                tvText.setMaxLines(3);
                viewExpand.setBackgroundResource(R.drawable.ic_arrow_drop_down_grey_600_24dp);
            }
        }
    }

    @Override
    public void onClickOptions(View v, Update update, Button buttonView) {
        PopupMenu popupMenu = new PopupMenu(this, buttonView);
        popupMenu.getMenuInflater().inflate(R.menu.update_card, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.menu_card_copytext:
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                    clipboard.setPrimaryClip(ClipData.newPlainText("Update Text", update.getText()));

                    showToast(R.string.toast_card_copiedtext);
                    break;
            }
            return false;
        });

        popupMenu.show();
    }

    private void showToast(@StringRes int resId) {
        showToast(getString(resId));
    }

    private void showToast(String text) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(this, text, Toast.LENGTH_LONG);
        toast.show();
    }

}
