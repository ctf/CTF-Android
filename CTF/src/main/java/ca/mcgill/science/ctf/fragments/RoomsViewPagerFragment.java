package ca.mcgill.science.ctf.fragments;

import java.util.Arrays;
import java.util.List;

import ca.allanwang.capsule.library.fragments.ViewPagerFragment;
import ca.allanwang.capsule.library.interfaces.CPage;
import ca.allanwang.capsule.library.item.PageItem;

/**
 * Created by Allan Wang on 2017-04-14.
 */

public class RoomsViewPagerFragment extends ViewPagerFragment {
    @Override
    protected List<CPage> setPages() {
        return Arrays.asList(new CPage[]{
                new PageItem(RoomJobFragment.getInstance(this, RoomJobFragment.Rooms._1B16), RoomJobFragment.Rooms._1B16.id),
                new PageItem(RoomJobFragment.getInstance(this, RoomJobFragment.Rooms._1B17), RoomJobFragment.Rooms._1B17.id),
                new PageItem(RoomJobFragment.getInstance(this, RoomJobFragment.Rooms._1B18), RoomJobFragment.Rooms._1B18.id)
        });
    }
}
