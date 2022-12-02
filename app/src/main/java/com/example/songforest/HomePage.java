package com.example.songforest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Locale;

public class HomePage extends AppCompatActivity implements SearchView.OnQueryTextListener {
    public static int RequestCode=1;
    public static ArrayList<MusicFiles> musicFiles;
    static boolean shuffleBoolean=false, repeatedBoolean= false;
    static ArrayList<MusicFiles> albums=new ArrayList<>();
    private String MY_SORT_PREF="SortOrder";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        permission();
    }

    public void permission(){
        if(ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(HomePage.this, new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, RequestCode);
        }
        else{
            musicFiles= getAllAudio(this);
            initViewPager();
            Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
        }
    }

    //if permission denied so repeated call
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(RequestCode==requestCode) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                musicFiles= getAllAudio(this);
                initViewPager();
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(HomePage.this, new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                }, RequestCode);
            }
        }
    }

    public void initViewPager(){
        ViewPager viewPager= findViewById(R.id.fragment_container);
        TabLayout tabLayout= findViewById(R.id.tab);

        ViewPagerAdapter viewPagerAdapter= new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragments(new SongFragment(), "Songs");
        viewPagerAdapter.addFragments(new AlbumFragment(), "Album");
        viewPagerAdapter.addFragments(new ProfileFragment(), "Profile");
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

    }



    public static class ViewPagerAdapter extends FragmentPagerAdapter {

        private ArrayList<Fragment> fragments;
        private ArrayList<String> titles;

        public ViewPagerAdapter(@NonNull FragmentManager fm){
            super (fm);
            this.fragments=new ArrayList<>();
            this.titles=new ArrayList<>();
        }

        public void addFragments(Fragment fragment, String title){
            fragments.add(fragment);
            titles.add(title);

        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }


    //will return all the songs from the storage
    public  ArrayList<MusicFiles> getAllAudio(Context context){
        SharedPreferences preferences=getSharedPreferences(MY_SORT_PREF,MODE_PRIVATE);
        String sortOrder=preferences.getString("sorting","sortByName");
        ArrayList<String> duplicate =new ArrayList<>();
        albums.clear();
        ArrayList<MusicFiles> temp_audioList= new ArrayList<>();
        String order=null;
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        switch (sortOrder){
            case "sortByName":
                order=MediaStore.MediaColumns.DISPLAY_NAME +" ASC";
                break;
            case "sortByDate":
                order=MediaStore.MediaColumns.DATE_ADDED +" DESC";
                break;
        }
        String[] projection = {
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA, //for path
                MediaStore.Audio.Media.ARTIST
        };

        Cursor cursor = context.getContentResolver().query(uri, projection,null,null,order);
        if(cursor!=null){
            while(cursor.moveToNext()){
                String album = cursor.getString(0);
                String title = cursor.getString(1);
                String duration = cursor.getString(2);
                String path = cursor.getString(3);
                String artist=cursor.getString(4);
                //will add all songs one by one
                MusicFiles musicFiles = new MusicFiles(path,title, artist, album, duration);
                temp_audioList.add(musicFiles);

                //to remove the duplicate albums
                if(!duplicate.contains(album)){
                    albums.add(musicFiles);
                    duplicate.add(album);
                }

            }
            cursor.close();
        }

        return temp_audioList;
    }
     //Menu option code for search bar and sorting
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search,menu);
        MenuItem menuItem=menu.findItem(R.id.search_bar);

        SearchView searchView=(SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(this);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        String userInput= newText.toLowerCase();
        ArrayList<MusicFiles> myFiles=new ArrayList<>();

        for(MusicFiles song :musicFiles){
            if(song.getTitle().toLowerCase().contains(userInput))
                myFiles.add(song);
        }
        SongFragment.musicAdapter.updateList(myFiles);
        return true;
    }

    //for selecting the sorting preference

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        SharedPreferences.Editor editor=getSharedPreferences(MY_SORT_PREF,MODE_PRIVATE).edit();

        switch (item.getItemId()){
            case R.id.nameSort:
                editor.putString("sorting","sortByName");
                editor.apply();
                this.recreate();
                break;
            case R.id.dateSort:
                editor.putString("sorting","sortByDate");
                editor.apply();
                this.recreate();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}