package kr.hs.emirim.uuuuri.ohdormitory.Fragment;

/**
 * Created by 유리 on 2017-10-01.
 */
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by Junyoung on 2016-06-23.
 */

public class TabPagerAdapter extends FragmentStatePagerAdapter {

    // Count number of tabs
    private int tabCount;

    public TabPagerAdapter(FragmentManager fm, int tabCount) {
        super(fm);
        this.tabCount = tabCount;
    }

    @Override
    public Fragment getItem(int position) {

        // Returning the current tabs
        switch (position) {
            case 0:
                NoticeFragment noticeFragment = new NoticeFragment();
                return noticeFragment;
            case 1:
                WashTimeFragment washTimeFragment = new WashTimeFragment();
                return washTimeFragment;
            case 2:
                SleepOutFragment sleepOutFragment = new SleepOutFragment();
                return sleepOutFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabCount;
    }
}
