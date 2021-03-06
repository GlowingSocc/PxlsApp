package space.pxls.android;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import space.pxls.Pxls;
import space.pxls.PxlsGame;
import java.net.URI;
import java.net.URISyntaxException;

public class AndroidLauncher extends AndroidApplication {
    public static final int CAPTCHA_REQUEST = 1;
    public static final int LOGIN_VIEW = 2;

    private PxlsGame game;
    private PxlsGame.CaptchaCallback captchaCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();

        game = new PxlsGame();
        Intent intent = getIntent();
        if (intent != null && intent.getAction().equals(Intent.ACTION_VIEW)) {
            
            try {
                String url = intent.getDataString();
                URI uri = new URI(url);
                if (!uri.getPath().startsWith("/auth")) {
                    game.startupURI = uri;
                }
            } catch (URISyntaxException e) {
            }
        }
        game.captchaRunner = new PxlsGame.CaptchaRunner() {
            @Override
            public void doCaptcha(String token, PxlsGame.CaptchaCallback captchaCallback) {
                Intent intent = new Intent(AndroidLauncher.this, CaptchaActivity.class);
                intent.putExtra("token", token);
                startActivityForResult(intent, CAPTCHA_REQUEST);

                AndroidLauncher.this.captchaCallback = captchaCallback;
            }
        };
        game.loginRunner = new PxlsGame.LoginRunner() {
            @Override
            public void doLogin(String method, String url) {
                if (method.equals("google") || method.equals("discord")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(AndroidLauncher.this, LoginActivity.class);
                    intent.putExtra("method", method);
                    intent.putExtra("url", url);
                    startActivityForResult(intent, LOGIN_VIEW);
                }
            }
        };

        View view = initializeForView(game, config);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(view, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1));

        setContentView(layout);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getAction().equals(Intent.ACTION_VIEW)) {
            try {
                String url = intent.getDataString();
                URI uri = new URI(url);
                if (uri.getPath().startsWith("/auth")) {
                    game.handleAuthenticationCallback(url);
                } else {
                    game.handleView(uri);
                }
            } catch (URISyntaxException e) {
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTCHA_REQUEST) {
            if (resultCode == RESULT_OK) {
                String token = data.getStringExtra("token");
                captchaCallback.done(token);
            }
            captchaCallback = null;
        } else if (requestCode == LOGIN_VIEW) {
            if (resultCode == RESULT_OK) {
                String url = data.getStringExtra("url");
                game.handleAuthenticationCallback(url);
            }
        }
    }
}
