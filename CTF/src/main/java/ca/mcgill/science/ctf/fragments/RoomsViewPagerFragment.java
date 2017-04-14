package ca.mcgill.science.ctf.fragments;

import java.util.ArrayList;
import java.util.List;

import ca.allanwang.capsule.library.fragments.ViewPagerFragment;
import ca.allanwang.capsule.library.interfaces.CPage;
import ca.allanwang.capsule.library.item.PageItem;
import ca.mcgill.science.ctf.R;

/**
 * Created by Allan Wang on 2017-04-14.
 */

public class RoomsViewPagerFragment extends ViewPagerFragment {
    @Override
    protected List<CPage> setPages() {
        List<CPage> list = new ArrayList<>(RoomJobFragment.Rooms.values().length);
        for (RoomJobFragment.Rooms room : RoomJobFragment.Rooms.values())
            list.add(new PageItem(RoomJobFragment.getInstance(this, room), room.id));
        return list;
    }

    @Override
    protected int getOffscreenPageLimit(int pageCount) {
        return pageCount - 1;
    }

    @Override
    public int getTitleId() {
        return R.string.roominfo;
    }
}
