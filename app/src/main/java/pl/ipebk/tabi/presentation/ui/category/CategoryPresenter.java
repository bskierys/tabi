/*
* author: Bartlomiej Kierys
* date: 2017-01-23
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.presentation.ui.category;

import android.view.View;

import javax.inject.Inject;

import pl.ipebk.tabi.presentation.localization.CategoryLocalizationHelper;
import pl.ipebk.tabi.presentation.model.AggregateId;
import pl.ipebk.tabi.presentation.ui.base.BasePresenter;
import pl.ipebk.tabi.readmodel.LicensePlateFinder;
import pl.ipebk.tabi.utils.RxUtil;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class CategoryPresenter extends BasePresenter<CategoryMvpView> {
    private CategoryLocalizationHelper localizationHelper;
    private LicensePlateFinder plateFinder;
    private Subscription searchSubscription;
    private String categoryKey;

    @Inject public CategoryPresenter(CategoryLocalizationHelper localizationHelper, LicensePlateFinder plateFinder) {
        this.localizationHelper = localizationHelper;
        this.plateFinder = plateFinder;
    }

    public void initCategory(String categoryKey) {
        this.categoryKey = categoryKey;

        getMvpView().showCategoryName(localizationHelper.formatCategory(categoryKey));
        getMvpView().showCategoryPlate(localizationHelper.getCategoryPlate(categoryKey));

        CategoryInfo info = new AutoValue_CategoryInfo(localizationHelper.getCategoryTitle(categoryKey),
                                                       localizationHelper.getCategoryBody(categoryKey),
                                                       localizationHelper.getCategoryLink(categoryKey),
                                                       localizationHelper.getCategoryIcon(categoryKey));
        getMvpView().showCategoryInfo(info);

        searchSubscription = Observable
                .just(categoryKey)
                .map(localizationHelper::getCategoryPlate)
                .subscribeOn(Schedulers.io())
                .flatMap(plate -> plateFinder.findPlacesForPlateStart(plate, null))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(cursor -> getMvpView().showPlates(cursor),
                           e -> Timber.e(e, "Error during searching for places", e));
    }

    public void loadPlaceDetails(View view, AggregateId placeId, String searchedPlate) {
        getMvpView().goToDetails(view, placeId, searchedPlate,
                                 localizationHelper.formatCategory(categoryKey),
                                 localizationHelper.getCategoryPlate(categoryKey));
    }

    @Override public void detachView() {
        super.detachView();

        RxUtil.unsubscribe(searchSubscription);
    }
}
