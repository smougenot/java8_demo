import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.ToIntFunction;


public class Lambda {


    private static String[] noms = {"Sylvain",
            "Sylvestre", "Sylvaine", "Sylvie",
    };

    private static int maxNbProjet_lambda(List<Developpeur> devs, boolean parallel) {
        return
                (parallel ? devs.parallelStream() : devs.stream())
                        .filter(d -> d.anneeEmbauche >= 2010)
                        .map((ToIntFunction<? super Developpeur>) d -> d.nbProjets)
                        .reduce(0, Math::max);
    }

    private static int maxNbProjet_se7(List<Developpeur> devs) {
        int maxPrj = 0;
        for (Developpeur d : devs) {
            if (d.anneeEmbauche >= 2010) {
                maxPrj = Math.max(maxPrj, d.nbProjets);
            }
        }
        return maxPrj;
    }

    public static void main(String[] args) {

        int[] nbDev = {
//                50_000_000
                70_000_000
//                100_000_000
        };
        for (int i : nbDev) {
            doIt(i);
        }
    }

    private static void doIt(int nbDev) {
        List<Developpeur> devs = getDeveloppeurs(nbDev);
        System.out.printf("Nb dev : %,d\n", nbDev);
        for (int i = 0; i < 4; i++) {
            doIt(devs);
        }
    }

    private static void doIt(List<Developpeur> devs) {
        System.out.printf("Nb dev : %,d\n", devs.size());

        long debut;
        int myRes;
        Stat[] lesStats = new Stat[3];

        debut = System.nanoTime();
        myRes = maxNbProjet_lambda(devs, false);
        lesStats[0] = new Stat("Lambda s", myRes, System.nanoTime() - debut);

        debut = System.nanoTime();
        myRes = maxNbProjet_lambda(devs, true);
        lesStats[1] = new Stat("Lambda p", myRes, System.nanoTime() - debut);

        debut = System.nanoTime();
        myRes = maxNbProjet_se7(devs);
        lesStats[2] = new Stat("foreach ", myRes, System.nanoTime() - debut);

        //afficher les statistiques
        lesStats[0].affiche(lesStats[2].getDuree());
        lesStats[1].affiche(lesStats[2].getDuree());
        lesStats[2].affiche(lesStats[2].getDuree());
    }

    private static List<Developpeur> getDeveloppeurs(int nb) {

        Random random = new Random();
        List<Developpeur> myRes = new ArrayList<>(nb);
        for (int i = 0; i < nb; i++) {
            myRes.add(
                    new Developpeur(noms[random.nextInt(noms.length)], 2000 + random.nextInt(14), random.nextInt(200))
            );
            if (i % 10_000_000 == 0) {
                System.out.printf("Dev progress %,d\n", i);
            }
        }
        return myRes;
    }

    private static class Stat {
        private final String nom;
        private final long duree;
        private final int resultat;

        private Stat(String nom, int resultat, long duree) {
            this.nom = nom;
            this.resultat = resultat;
            this.duree = duree;
        }

        public long getDuree() {
            return duree;
        }

        public void affiche(long tempsReference) {
            System.out.printf("%s\tmax projets %,d\t, en %,d ns\t %3.0f%%\n", nom, resultat, duree, new BigDecimal(duree).divide(new BigDecimal(tempsReference), 4, RoundingMode.HALF_UP).movePointRight(2));
        }
    }

    private static class Developpeur {
        String nom;
        int anneeEmbauche;
        int nbProjets;

        Developpeur(String nom, int anneeEmbauche, int nbProjets) {
            this.nom = nom;
            this.anneeEmbauche = anneeEmbauche;
            this.nbProjets = nbProjets;
        }
    }
}
