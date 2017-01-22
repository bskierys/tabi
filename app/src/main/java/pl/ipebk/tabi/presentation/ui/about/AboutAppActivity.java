/*
* author: Bartlomiej Kierys
* date: 2016-12-26
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.presentation.ui.about;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.LibsConfiguration;
import com.mikepenz.aboutlibraries.entity.Library;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.ipebk.tabi.R;
import pl.ipebk.tabi.presentation.ui.base.BaseActivity;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class AboutAppActivity extends BaseActivity {
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.libraries_list) RecyclerView librariesView;
    private LibraryAdapter adapter;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        toolbar.setVisibility(View.GONE);

        initLibraries();
        getLoadLibsObservable().observeOn(AndroidSchedulers.mainThread())
                               .subscribeOn(Schedulers.newThread())
                               .subscribe(listItems -> {
                                  adapter.appendList(listItems);
                                  adapter.notifyDataSetChanged();
                              }, error -> Timber.w(error, "Activity already closed"));
    }

    @OnClick(R.id.btn_back) public void onBackButton() {
        onBackPressed();
    }

    private void initLibraries() {
        librariesView.setHasFixedSize(true);
        librariesView.setItemAnimator(LibsConfiguration.getInstance().getItemAnimator());
        librariesView.setLayoutManager(new LinearLayoutManager(this));

        List<LibraryAdapter.LibsItem> adapterItems = new ArrayList<>();
        adapterItems.add(new LibraryAdapter.LibsItem(null));
        adapter = new LibraryAdapter(this, adapterItems);
        librariesView.setAdapter(adapter);
    }

    private Observable<List<LibraryAdapter.LibsItem>> getLoadLibsObservable() {
        return Observable.defer(() -> Observable.just(getItems()));
    }

    private List<LibraryAdapter.LibsItem> getItems() {
        Libs libs = new Libs(this);
        List<LibraryAdapter.LibsItem> adapterItems = new ArrayList<>();
        List<Library> libraries = libs.prepareLibraries(this, new String[]{}, null, true, true);
        for (Library library : libraries) {
            adapterItems.add(new LibraryAdapter.LibsItem(library));
        }
        return adapterItems;
    }
}
