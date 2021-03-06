package com.thracecodeinc.guessTheFlag;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * Created by Samurai on 4/19/15.
 */
public class OfflineGameClass extends Activity {
    private List<String> fileNameList; // flag file names
    private List<String> quizCountriesList;
    private Map<String, Boolean> regionsMap;
    private String correctAnswer;
    private int totalGuesses; // number of guesses made
    private int correctAnswers; // number of correct guesses
    private int guessRows;
    private Random random;
    private Handler handler;
    private Animation shakeAnimation;

    private TextView answerTextView;
    private TextView questionNumberTextView;
    private ImageView flagImageView;
    private TableLayout buttonTableLayout;
    private boolean startedByUser;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.flags_offline_game);

        String actionBarTitle = getString(R.string.offline_mode);
        getActionBar().setTitle(Html.fromHtml("<font color='#20b2aa'>" + actionBarTitle + "</font>"));


        guessRows = getIntent().getIntExtra("guessRows", guessRows);

        fileNameList = new ArrayList<String>();
        quizCountriesList = new ArrayList<String>();
        regionsMap = new HashMap<String, Boolean>();

        guessRows = getIntent().getIntExtra("guessRows",2);
        startedByUser = getIntent().getBooleanExtra("startedByUser",false);
        random = new Random();
        handler = new Handler();
        shakeAnimation =
                AnimationUtils.loadAnimation(this, R.anim.incorrect_shake);
        shakeAnimation.setRepeatCount(3); String[] regionNames =
            getResources().getStringArray(R.array.regionsList);
        for (String region : regionNames )
            regionsMap.put(region, true);
        regionsMap.putAll((HashMap<String,Boolean>) getIntent().getSerializableExtra("regionsMap"));
        questionNumberTextView =
                (TextView) findViewById(R.id.questionNumberTextView);
        flagImageView = (ImageView) findViewById(R.id.flagImageView);
        buttonTableLayout =
                (TableLayout) findViewById(R.id.buttonTableLayout);
        answerTextView = (TextView) findViewById(R.id.answerTextView);
        questionNumberTextView.setText(
                getResources().getString(R.string.question) + " 1 " +
                        getResources().getString(R.string.of) + " 10");

        resetQuiz();
    }
    private void resetQuiz()
    {
        if (SharedMethods.isOnline(OfflineGameClass.this) && !startedByUser)
            SharedMethods.networkModePopup(OfflineGameClass.this, (HashMap) regionsMap, guessRows);

        AssetManager assets = getAssets();
        fileNameList.clear();

        try
        {
            Set<String> regions = regionsMap.keySet();

            for (String region : regions)
            {
                if (regionsMap.get(region))
                {               String[] paths = assets.list(region);

                    for (String path : paths)
                        fileNameList.add(path.replace(".png", ""));
                }
            }
        }
        catch (IOException e)
        {
            //Log.e(TAG, "Error loading image file names", e);
        }

        correctAnswers = 0;
        totalGuesses = 0;
        quizCountriesList.clear();

        int flagCounter = 1;
        int numberOfFlags = fileNameList.size();
        while (flagCounter <= 10)
        {
            int randomIndex = random.nextInt(numberOfFlags);
            String fileName = fileNameList.get(randomIndex);
            if (!quizCountriesList.contains(fileName))
            {
                quizCountriesList.add(fileName);
                ++flagCounter;
            }}
        loadNextFlag();
    }
    private void loadNextFlag()
    {
        String nextImageName = quizCountriesList.remove(0);
        correctAnswer = nextImageName;

        answerTextView.setText("");
        questionNumberTextView.setText(
                getResources().getString(R.string.question) + " " +
                        (correctAnswers + 1) + " " +
                        getResources().getString(R.string.of) + " 10");
        String region =
                nextImageName.substring(0, nextImageName.indexOf('-'));
        AssetManager assets = getAssets(); // get app's AssetManager
        InputStream stream;
        try
        {
            stream = assets.open(region + "/" + nextImageName + ".png");

            Drawable flag = Drawable.createFromStream(stream, nextImageName);
            flagImageView.setImageDrawable(flag);
        }
        catch (IOException e)
        {
            //Log.e(TAG, "Error loading " + nextImageName, e);
        }
        for (int row = 0; row < buttonTableLayout.getChildCount(); ++row)
            ((TableRow) buttonTableLayout.getChildAt(row)).removeAllViews();

        Collections.shuffle(fileNameList);

        int correct = fileNameList.indexOf(correctAnswer);
        fileNameList.add(fileNameList.remove(correct));

        LayoutInflater inflater = (LayoutInflater) getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);


        for (int row = 0; row < guessRows; row++)
        {
            TableRow currentTableRow = getTableRow(row);

            for (int column = 0; column < 2; column++)
            {
                Button newGuessButton =
                        (Button) inflater.inflate(R.layout.flags_guess_button, null);
                String fileName = fileNameList.get((row * 2) + column);
                newGuessButton.setText(getCountryNameFromStrings(this, fileName));
                newGuessButton.setOnClickListener(guessButtonListener);
                currentTableRow.addView(newGuessButton);
            }
        }
        int row = random.nextInt(guessRows);
        int column = random.nextInt(2);
        TableRow randomTableRow = getTableRow(row);
        ((Button)randomTableRow.getChildAt(column)).setText(getCountryNameFromStrings(this, correctAnswer));
    }
    private TableRow getTableRow(int row)
    {
        return (TableRow) buttonTableLayout.getChildAt(row);
    }
    private String getCountryName(String name)
    {
        return name.substring(name.indexOf('-') + 1).replace('_', ' ');
    }
    private void submitGuess(Button guessButton)
    {
        String guess = guessButton.getText().toString();
        String answer = getCountryNameFromStrings(this, correctAnswer);
        ++totalGuesses;
        if (guess.equals(answer))
        {
            ++correctAnswers;
            answerTextView.setText(answer);
            answerTextView.setTextColor(
                    getResources().getColor(R.color.correct_answer));
            guessButton.setTextColor(getResources().getColor(R.color.correct_answer));
            disableButtons();
            if (correctAnswers == 10) {
                double currentHighScore = SharedMethods.readHighScore(OfflineGameClass.this);
                float scorePrcntg = 1000 / (float) totalGuesses;

                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setTitle(R.string.reset_quiz);
                builder.setIcon(SharedMethods.emoticon(scorePrcntg));

                builder.setTitle(R.string.new_score);

                if (currentHighScore < scorePrcntg) {

                    SharedMethods.writeHighScore(OfflineGameClass.this, scorePrcntg);
                    builder.setMessage(String.format("%d %s, %.02f%% %s \n %s - %.02f%%",
                            totalGuesses, getResources().getString(R.string.guesses),
                            (scorePrcntg),
                            getResources().getString(R.string.correct),
                            getResources().getString(R.string.new_score), scorePrcntg));
                } else{
                    builder.setMessage(String.format("%d %s, %.02f%% %s",
                            totalGuesses, getResources().getString(R.string.guesses),
                            (scorePrcntg),
                            getResources().getString(R.string.correct)));
                }

                builder.setCancelable(false);
                builder.setPositiveButton(R.string.reset_quiz,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                resetQuiz();
                            }
                        }
                );
                AlertDialog resetDialog = builder.create();
                resetDialog.show();
            }
            else
            {  handler.postDelayed(
                    new Runnable() {
                        @Override
                        public void run() {
                            loadNextFlag();
                        }
                    }, 1000);
            }
        }
        else
        {  flagImageView.startAnimation(shakeAnimation);
            answerTextView.setText(R.string.incorrect_answer);
            answerTextView.setTextColor(
                    getResources().getColor(R.color.incorrect_answer));
            guessButton.setEnabled(false);
            guessButton.setTextColor(getResources().getColor(R.color.translucent_black));
        }
    }

    private void disableButtons()
    {
        for (int row = 0; row < buttonTableLayout.getChildCount(); ++row)
        {
            TableRow tableRow = (TableRow) buttonTableLayout.getChildAt(row);
            for (int i = 0; i < tableRow.getChildCount(); ++i)
                tableRow.getChildAt(i).setEnabled(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.flags_options_menu_myactivity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_item_return_home:
                SharedMethods.quitGamePopup(this);
                break;
            case R.id.menu_new_game:
                resetGamePopup();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
    private View.OnClickListener guessButtonListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            submitGuess((Button) v);
        }
    };

    public void resetGamePopup(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setCustomTitle(SharedMethods.customText(getApplicationContext()));

        builder.setCancelable(true);
        builder.setPositiveButton(getString(R.string.yes),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        resetQuiz();
                    }
                }
        );
        builder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog resetDialog = builder.create();
        resetDialog.show();
    }

    public static String getCountryNameFromStrings(Activity a, String fileName)
    {
        int resId = a.getResources().getIdentifier(fileName.substring(fileName.indexOf("-") + 1), "string", a.getPackageName());
        Log.d("Country 1", "Country: " + fileName.substring(fileName.indexOf("-") + 1));
        return a.getString(resId);

    }



}
