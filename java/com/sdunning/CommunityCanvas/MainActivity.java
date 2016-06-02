package com.sdunning.CommunityCanvas;

import android.app.*;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.*;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import java.util.UUID;
import android.provider.MediaStore;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.View.OnClickListener;
import android.widget.Toast;
import android.view.*;
import android.widget.*;
import android.widget.SeekBar.*;

public class MainActivity extends AppCompatActivity implements OnClickListener {

	private DrawingView drawView;
	private Toolbar toolbar;
	private ImageButton currPaint, drawBtn, eraseBtn, newBtn, saveBtn;
	private float smallBrush, mediumBrush, largeBrush;


	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);

        toolbar = (Toolbar)findViewById(R.id.menu_bar);
        setSupportActionBar(toolbar);

		drawView = (DrawingView)findViewById(R.id.drawing);
		LinearLayout paintLayout = (LinearLayout)findViewById(R.id.paint_colors);
		currPaint = (ImageButton)paintLayout.getChildAt(0);
		currPaint.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.paint_pressed, null));

		smallBrush = getResources().getInteger(R.integer.small_size);
		mediumBrush = getResources().getInteger(R.integer.medium_size);
		largeBrush = getResources().getInteger(R.integer.large_size);

		drawBtn = (ImageButton)findViewById(R.id.draw_btn);
		drawBtn.setOnClickListener(this);
		drawView.setBrushSize(mediumBrush);

		eraseBtn = (ImageButton)findViewById(R.id.erase_btn);
		eraseBtn.setOnClickListener(this);

		newBtn = (ImageButton)findViewById(R.id.new_btn);
		newBtn.setOnClickListener(this);

		saveBtn = (ImageButton)findViewById(R.id.save_btn);
		saveBtn.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    public int getPixels(int pixels) {
        float scale;

        scale = getResources().getDisplayMetrics().density;
        return (int) (pixels * scale + 0.5f);
    }

	public void paintClicked(View view){
		drawView.setErase(false);
		drawView.setBrushSize(drawView.getLastBrushSize());
		if(view!=currPaint){
			ImageButton imgView = (ImageButton)view;
			String color = view.getTag().toString();
			drawView.setColor(color);
			imgView.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.paint_pressed, null));
			currPaint.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.paint, null));
			currPaint=(ImageButton)view;
		}
	}

	@Override
	public void onClick(View view){
		if(view.getId()==R.id.draw_btn){
            drawView.setErase(false);
			final Dialog brushDialog = new Dialog(this);
			brushDialog.setTitle("Brush size: " + (int)drawView.getLastBrushSize());
			brushDialog.setContentView(R.layout.brush_chooser);

			TextView sizeView = (TextView)brushDialog.findViewById(R.id.sizeView);
			SeekBar size = (SeekBar)brushDialog.findViewById(R.id.size);
			size.setProgress((int)drawView.getLastBrushSize());
			sizeView.setText(String.valueOf((int)drawView.getLastBrushSize()));
            final ImageView brushExample = (ImageView)brushDialog.findViewById(R.id.brush_example);
            brushExample.setLayoutParams(new LinearLayout.LayoutParams(getPixels((int)drawView.getLastBrushSize()),
                                                                       getPixels((int)drawView.getLastBrushSize())));
            Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.brush_size_display, null);
            drawable.setColorFilter(drawView.getColor(), PorterDuff.Mode.SRC_ATOP);
            brushExample.setImageDrawable(drawable);
			size.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){
				    TextView sizeIndicator = (TextView)brushDialog.findViewById(R.id.sizeView);

				    int progress = 0;
					@Override
					public void onProgressChanged(SeekBar seekBar, int progressValue, boolean fromUser) {
						progress = progressValue;
						if(progress < 1) {
							progress = 1;
							setProgress(progress);
						}
						sizeIndicator.setText(String.valueOf(progress));
                        brushDialog.setTitle("Brush size: " + progress);
                        brushExample.setLayoutParams(new LinearLayout.LayoutParams(getPixels(progress), getPixels(progress)));
					}

					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {
					}

					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {
						sizeIndicator.setText(String.valueOf(progress));
						if(progress < 1) {
							progress = 1;
							setProgress(progress);
						}
						drawView.setBrushSize(progress);
						drawView.setLastBrushSize(progress);
					}
				});
			Button ok = (Button)brushDialog.findViewById(R.id.brushConfirm);
			ok.setOnClickListener(new OnClickListener(){
				    @Override
					public void onClick(View view) {
						brushDialog.dismiss();
					}
			    });

			brushDialog.show();
		}

		else if(view.getId()==R.id.erase_btn){
            drawView.setErase(true);
			final Dialog brushDialog = new Dialog(this);
            brushDialog.setTitle("Eraser size: " + (int)drawView.getLastBrushSize());
            brushDialog.setContentView(R.layout.brush_chooser);

            TextView sizeView = (TextView)brushDialog.findViewById(R.id.sizeView);
            SeekBar size = (SeekBar)brushDialog.findViewById(R.id.size);
            size.setProgress((int)drawView.getLastBrushSize());
            sizeView.setText(String.valueOf((int)drawView.getLastBrushSize()));
            final ImageView brushExample = (ImageView)brushDialog.findViewById(R.id.brush_example);
            brushExample.setLayoutParams(new LinearLayout.LayoutParams(getPixels((int)drawView.getLastBrushSize()),
                    getPixels((int)drawView.getLastBrushSize())));
            Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.brush_size_display, null);
            drawable.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP);
            brushExample.setImageDrawable(drawable);

            size.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){
                TextView sizeIndicator = (TextView)brushDialog.findViewById(R.id.sizeView);

                int progress = 0;
                @Override
                public void onProgressChanged(SeekBar seekBar, int progressValue, boolean fromUser) {
                    progress = progressValue;
                    if(progress < 1) {
                        progress = 1;
                        setProgress(progress);
                    }
                    sizeIndicator.setText(String.valueOf(progress));
                    brushDialog.setTitle("Eraser size: " + progress);
                    brushExample.setLayoutParams(new LinearLayout.LayoutParams(getPixels(progress), getPixels(progress)));
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    sizeIndicator.setText(String.valueOf(progress));
                    if(progress < 1) {
                        progress = 1;
                        setProgress(progress);
                    }
                    drawView.setBrushSize(progress);
                    drawView.setLastBrushSize(progress);
                }
            });
            Button ok = (Button)brushDialog.findViewById(R.id.brushConfirm);
            ok.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View view) {
                    brushDialog.dismiss();
                }
            });

            brushDialog.show();
		}

		else if(view.getId()==R.id.new_btn){
			AlertDialog.Builder newDialog = new AlertDialog.Builder(this);
			newDialog.setTitle("New drawing");
			newDialog.setMessage("Start new drawing (you will lose the current drawing)?");
			newDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface dialog, int which){
						drawView.startNew();
						dialog.dismiss();
					}
				});
			newDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface dialog, int which){
						dialog.cancel();
					}
				});
			newDialog.show();
		}

		else if(view.getId()==R.id.save_btn){
            AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);
			saveDialog.setTitle("Save drawing");
			saveDialog.setMessage("Save drawing to device Gallery?");
			saveDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface dialog, int which){
						drawView.setDrawingCacheEnabled(true);
						String imgSaved = MediaStore.Images.Media.insertImage(
							getContentResolver(), drawView.getDrawingCache(),
							UUID.randomUUID().toString()+".png", "drawing");
						if(imgSaved!=null){
							Toast savedToast = Toast.makeText(getApplicationContext(),
								               "Drawing saved to Gallery!", Toast.LENGTH_SHORT);
							savedToast.show();
						}
						else{
							Toast unsavedToast = Toast.makeText(getApplicationContext(),
									             "Oops! Image could not be saved.", Toast.LENGTH_SHORT);
							unsavedToast.show();
						}
						drawView.destroyDrawingCache();
					}
				});
			saveDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface dialog, int which){
						dialog.cancel();
					}
				});
			saveDialog.show();
		}
	}
}
