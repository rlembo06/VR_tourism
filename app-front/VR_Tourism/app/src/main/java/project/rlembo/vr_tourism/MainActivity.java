package project.rlembo.vr_tourism;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * TUTO :
 * http://www.tutos-android.com/service-android
 * https://www.sitepoint.com/retrofit-a-simple-http-client-for-android-and-java/
 * https://spring.io/guides/gs/consuming-rest/
 * http://square.github.io/okhttp/
 */
public class MainActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button serviceBtn = (Button) findViewById(R.id.serviceBtn);
        serviceBtn.setOnClickListener( new View.OnClickListener()
        {

            @Override
            public void onClick(View actuelView){
                startService(new Intent(MainActivity.this, MonPremierService.class));
            }
        });


        Button serviceVideo = (Button) findViewById(R.id.serviceVideo);
        serviceVideo.setOnClickListener( new View.OnClickListener()
        {

            @Override
            public void onClick(View actuelView){
                startService(new Intent(MainActivity.this, MonPremierService.class));

                RestTemplate restTemplate = new RestTemplate();
                Quote quote = restTemplate.getForObject("http://gturnquist-quoters.cfapps.io/api/random", Quote.class);
                System.out.println(quote.toString());
            }
        });
    }
}
