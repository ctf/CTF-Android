package ca.mcgill.science.ctf.fragments;

import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;

import java.util.List;

import ca.allanwang.capsule.library.interfaces.CPageFragment;
import ca.mcgill.science.ctf.R;
import ca.mcgill.science.ctf.api.ITEPID;
import ca.mcgill.science.ctf.api.PrintData;
import ca.mcgill.science.ctf.fragments.base.BaseFragment;
import ca.mcgill.science.ctf.fragments.base.BasePrintJobFragment;
import retrofit2.Call;

public class RoomJobFragment extends BasePrintJobFragment implements CPageFragment {

    private Rooms room;

    @Override
    public void onSelected(int oldPosition, int newPosition) {
        if (mSRV == null) return; //not ready yet
        if (oldPosition == newPosition)
            mSRV.getRecyclerView().scrollToPosition(0); //if tab is selected and tapped again, scroll back to the top
    }

    enum Rooms {
        _1B16("1B16", R.string.string_1b16),
        _1B17("1B17", R.string.string_1b17),
        _1B18("1B18", R.string.string_1b18);

        String name;
        @StringRes
        int id;

        Rooms(String s, @StringRes int id) {
            this.name = s;
            this.id = id;
        }
    }

    private static final String TAG_ROOM = "room_number";


    public static Fragment getInstance(RoomsViewPagerFragment parent, Rooms room) {
        Fragment fragment = BaseFragment.getFragment(getToken(parent), getShortUser(parent), new RoomJobFragment());
        fragment.getArguments().putSerializable(TAG_ROOM, room);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        room = (Rooms) getArguments().getSerializable(TAG_ROOM);
    }

    @Override
    protected Call<List<PrintData>> getAPICall(ITEPID api) {
        return api.getPrintQueue(room.name, 50);
    }

    @Override
    public int getTitleId() {
        return R.string.roominfo;
    }

}

