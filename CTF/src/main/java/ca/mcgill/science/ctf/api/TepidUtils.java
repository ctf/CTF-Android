package ca.mcgill.science.ctf.api;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by Allan Wang on 30/04/2017.
 */

public class TepidUtils {

    /**
     * Gets {@link FullUser} which is a combination of
     * User, Quota (boolean), and PrintJobs
     */
    public static void getFullUser(String shortUser, SingleObserver<FullUser> fullUserObserver) {
        ITepid caller = TepidApi.Companion.getInstanceDangerously();
        Single<User> user = caller.getUserObservable(shortUser).singleOrError();
        Single<Integer> quota = caller.getQuotaObservable(shortUser).singleOrError();
        Single<FullUser> fullUserObservable = Single.zip(user, quota, FullUser::new);

        fullUserObservable.observeOn(AndroidSchedulers.mainThread()).subscribe(fullUserObserver);
    }
}
