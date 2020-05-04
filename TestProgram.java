import java.io.File;
import java.io.FileNotFoundException;

class TestProgram {
    public static void main(String[] a) {
        Labyrint l;
        try {
            l = Labyrint.lesFraFil(new File(a[0]));
        } catch (FileNotFoundException e) {
            System.out.println("Ugyldig fil: "+a[0]);
            return;
        }
        System.out.println(l);
        Liste<String> li = l.finnUtveiFra(5,3);
        for (String u : li) {
            System.out.println(u);
        }
    }
}
