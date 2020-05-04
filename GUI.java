import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.event.*;
import javafx.scene.layout.GridPane;
import javafx.scene.control.Button;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class GUI extends Application {
    RuteG[][] ruter;
    Labyrint l;
    int hoyde;
    int bredde;
    Text status = new Text("Velg en hvit rute");
    Liste<String> utveier;
    int utveinr = 0;
    /**
     * Konverterer losning-String fra oblig 5 til en boolean[][]-representasjon
     * av losningstien.
     * @param losningString String-representasjon av utveien
     * @param bredde        bredde til labyrinten
     * @param hoyde         hoyde til labyrinten
     * @return              2D-representasjon av rutene der true indikerer at
     *                      ruten er en del av utveien.
     */
    static boolean[][] losningStringTilTabell(String losningString, int bredde, int hoyde) {
        boolean[][] losning = new boolean[hoyde][bredde];
        java.util.regex.Pattern p = java.util.regex.Pattern.compile("\\(([0-9]+),([0-9]+)\\)");
        java.util.regex.Matcher m = p.matcher(losningString.replaceAll("\\s",""));
        while (m.find()) {
            int x = Integer.parseInt(m.group(1));
            int y = Integer.parseInt(m.group(2));
            losning[y][x] = true;
        }
        return losning;
    }
    class VelgBehandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent e) {
            RuteG valgtRute = (RuteG) e.getSource();
            if (valgtRute.erHvit()) {
                utveinr = 0;
                String utvei = "";
                try {
                    utveier = l.finnUtveiFra(valgtRute.coords()[0],valgtRute.coords()[1]);
                    utvei = utveier.hent(0);
                    status.setText(String.format("Viser utvei nr. "+Integer.valueOf(1)+" fra (%d, %d)",valgtRute.coords()[0],valgtRute.coords()[1]));
                } catch (UgyldigListeIndeks err) {
                    status.setText("Fant ingen utveier. Velg en annen rute.");
                }
                boolean[][] skalFarges = losningStringTilTabell(utvei,bredde,hoyde);
                for (int i = 0; i < bredde; i++) {
                    for (int j = 0; j < hoyde; j++) {
                        ruter[i][j].gjorHvit();
                        if (skalFarges[j][i]) {
                            ruter[i][j].gjorRod();
                        }
                    }
                }
            } else {
                status.setText("Velg en HVIT rute.");
            }
        }
    }
    class NesteBehandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent e) {
            utveinr++;
            if (utveinr >= utveier.stoerrelse()) {
                utveinr = 0;
            }
            status.setText("Viser utvei nr. "+(utveinr+1));
            boolean[][] skalFarges = losningStringTilTabell(utveier.hent(utveinr),bredde,hoyde);
            for (int i = 0; i < bredde; i++) {
                for (int j = 0; j < hoyde; j++) {
                    ruter[i][j].gjorHvit();
                    if (skalFarges[j][i]) {
                        ruter[i][j].gjorRod();
                    }
                }
            }
        }
    }
    class RuteG extends Button {
        char t;
        int x;
        int y;
        RuteG(char tegn, int a, int b) {
            super(" ");
            t = tegn;
            x = a;
            y = b;
            setPrefSize(40,40);
            if (t == '#') {
                this.setStyle("-fx-background-color: Black");
            } else {
                this.setStyle("-fx-background-color: #eeeeee");
            }
        }
        void gjorRod() {
            if (t == '.')
                this.setStyle("-fx-background-color: Red");
        }
        void gjorHvit() {
            if (t == '.')
                this.setStyle("-fx-background-color: #eeeeee");
        }
        int[] coords() {
            int[] r = {x,y};
            return r;
        }
        boolean erHvit() {
            return (t == '.');
        }
    }
    @Override
    public void start(Stage st) {
        File f = new FileChooser().showOpenDialog(st);
        String ls = "";
        try {
            l = Labyrint.lesFraFil(f);
            ls = l.toString();
        } catch (FileNotFoundException e) {
            System.exit(0);
        }
        GridPane grid = new GridPane();
        grid.setGridLinesVisible(true);
        Scanner scn = new Scanner(ls);
        String parametere = scn.nextLine();
        Scanner scn2 = new Scanner(parametere);
        hoyde = scn2.nextInt();
        bredde = scn2.nextInt();
        ruter = new RuteG[bredde][hoyde];
        int i = 0;
        VelgBehandler vb = new VelgBehandler();
        while (scn.hasNextLine()) {
            String ln = scn.nextLine();
            for (int j = 0; j < ln.length(); j++) {
                char x = ln.charAt(j);
                RuteG r = new RuteG(x,j,i);
                r.setOnAction(vb);
                ruter[j][i] = r;
                grid.add(r, j, i);
            }
            i++;
        }
        grid.setLayoutX(10);
        grid.setLayoutY(50);
        //status.setFont(Font.font("Monospace",20));
        status.setFont(new Font(20));
        status.setX(10);
        status.setY(35);
        NesteBehandler nb = new NesteBehandler();
        Button neste = new Button("Neste utvei");
        neste.setLayoutX(600);
        neste.setLayoutY(20);
        neste.setOnAction(nb);
        Pane p = new Pane();
        p.getChildren().add(status);
        p.getChildren().add(grid);
        p.getChildren().add(neste);
        Scene sc = new Scene(p);
        st.setTitle("Grafisk labyrintløser");
        st.setScene(sc);
        st.show();
    }
    public static void main(String[] a) {
        launch(a);
    }
}
