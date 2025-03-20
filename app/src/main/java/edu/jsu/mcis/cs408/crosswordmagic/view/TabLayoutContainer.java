package edu.jsu.mcis.cs408.crosswordmagic.view;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import edu.jsu.mcis.cs408.crosswordmagic.R;

public class TabLayoutContainer extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tab_container, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ViewPager2 viewPager = view.findViewById(R.id.pager);
        TabLayout tabLayout = view.findViewById(R.id.tab_layout);
        FragmentStateAdapter adapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0) tab.setText("Puzzle");
            else if (position == 1) tab.setText("Clues");
        }).attach();
    }

    private static class ViewPagerAdapter extends FragmentStateAdapter {
        public ViewPagerAdapter(@NonNull Fragment fragment) {
            super(fragment);
        }
        @NonNull
        @Override
        public Fragment createFragment(int position) {
            if (position == 0) return new PuzzleFragment();
            else return new ClueFragment();
        }
        @Override
        public int getItemCount() {
            return 2; // Two tabs: Puzzle and Clues
        }
    }
}
