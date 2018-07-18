package falcon.assassin.ph.foodblog;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import falcon.assassin.ph.foodblog.account.setup.SetupActivity;
import falcon.assassin.ph.foodblog.fragment.AcccountFragment;
import falcon.assassin.ph.foodblog.fragment.HomeFragment;
import falcon.assassin.ph.foodblog.fragment.NotificationFragment;
import falcon.assassin.ph.foodblog.login.LoginActivity;
import falcon.assassin.ph.foodblog.post.PostActivity;
import falcon.assassin.ph.foodblog.view.adapter.ViewPagerAdapter;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private Toolbar mToolBar;
    private NavigationView navigationView;
    private FloatingActionButton btnFloatAdd;
    private BottomNavigationView bottomNavigation;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    private HomeFragment homeFragment;
    private AcccountFragment acccountFragment;
    private NotificationFragment notificationFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // initialize content
        init();
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        // Check if user is signed in
        if (currentUser == null) {
            Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(loginIntent);
            finish();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (mToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void init() {

        homeFragment = new HomeFragment();
        notificationFragment = new NotificationFragment();
        acccountFragment = new AcccountFragment();

        bottomNavigation = findViewById(R.id.bottomNavigation);

        // Set navigation onclick
        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {

                    case R.id.nav_bottom_home:

                        replaceFragment(homeFragment);

                        return true;

                    case R.id.nav_bottom_notification:

                        replaceFragment(notificationFragment);

                        return true;

                    case R.id.nav_bottom_account:

                        replaceFragment(acccountFragment);

                        return true;

                    default:

                        return false;

                }

            }
        });

/*
        viewPager = findViewById(R.id.viewpagerlayout);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        tabLayout = findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);

        //on click
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                Toast.makeText(MainActivity.this, "Tab 1", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });*/

        mToolBar = findViewById(R.id.mainActionBar);
        setSupportActionBar(mToolBar);

        // Initialize FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        mDrawerLayout = findViewById(R.id.mainDrawerLayout);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.open, R.string.close);

        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("FoodBook");

        btnFloatAdd = findViewById(R.id.btn_float_add);

        btnFloatAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent postIntent = new Intent(MainActivity.this, PostActivity.class);
                startActivity(postIntent);
            }
        });

        // Navigation View
        navigationView = findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {

                    case R.id.nav_account:

                        Log.d("tag", "My Account");
                        Intent accountIntent = new Intent(MainActivity.this,
                                SetupActivity.class);
                        startActivity(accountIntent);

                        break;

                    case R.id.nav_logout:

                        // Log out the user and redirects to login activity
                        mAuth.signOut();
                        redirect();
                        Log.d("tag", "Log out");
                        break;
                }

                return false;
            }
        });
    }

    /**
     * Redirect to main activity
     *
     */
    protected void redirect() {

        // Redirect to Login activity
        Intent redirectIntentLogin = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(redirectIntentLogin);
        finish();
    }

    protected void replaceFragment(Fragment fragment) {

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_container, fragment);
        fragmentTransaction.commit();
    }
}
