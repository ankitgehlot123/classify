package classify;

import com.ibm.watson.developer_cloud.visual_recognition.v3.VisualRecognition;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ClassifiedImages;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ClassifyOptions;
import java.awt.Image;
import java.io.File;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingWorker;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Ankit
 */
public class controller {

    ClassifiedImages result;

    public ClassifiedImages classifier(String input, JLabel lb, JFrame j) {

        // TODO add your handling code here:
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                VisualRecognition service = new VisualRecognition("2016-05-20");
                service.setApiKey("45428eaa1ba0f68573bdc8728c100e392d717c5a");
                showLoader(lb);
                setProgress(15);
                System.out.println("Classify an image");

                ClassifyOptions options = new ClassifyOptions.Builder()
                        .imagesFile(new File(input))
                        .build();
                setProgress(25);
                result = service.classify(options).execute();
                setProgress(50);
                System.out.println(result + "\n" + input);
                parse(result.getImages().toString(), ImageIO.read(new File(input)));
                setProgress(100);
                URL url = this.getClass().getResource("/done.png");
                lb.setIcon(new ImageIcon(url));
                if (j.toString().startsWith("classify.browse")) {
                    j.dispose();
                }

                return null;
            }

            private String absUrl(String input) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        };
        worker.execute();
        return result;
    }

    public void showLoader(JLabel main) {
        URL url = this.getClass().getResource("/spinner.gif");
        main.setIcon(new ImageIcon(url));
        main.setVisible(true);
    }

    public void parse(String str, Image image) throws JSONException {

        data = new ArrayList<>();
        score = new ArrayList<>();
        JSONArray obj = new JSONArray(str);
        obj = obj.getJSONObject(0).getJSONArray("classifiers").getJSONObject(0).getJSONArray("classes");
        len = obj.length();
        for (int i = 0; i < obj.length(); i++) {

            data.add(obj.getJSONObject(i).getString("class"));
            score.add(obj.getJSONObject(i).getDouble("score"));
            System.out.println(data.get(i) + " : " + score.get(i));

        }

        java.awt.EventQueue.invokeLater(() -> {
            new Result(data, score, len, image).setVisible(true);
        });

    }
    ArrayList<String> data;
    ArrayList<Double> score;
    int len;
}
