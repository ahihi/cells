package utils;

/**
    * Matemaattisia apuvälineitä sisältävä työkaluluokka.
    */
abstract public class Maths {
    /**
        * Laskee jakojäännöksen. Toisin kuin Javan sisäänrakennettu jakojäännöslasku, tämän metodin tulos on aina intervallissa [0, b-1], mikä osoittautuu hyödylliseksi soluautomaatin toteutuksessa.
        *
        * @param a Osoittaja.
        * @param b Nimittäjä.
        *
        * @return Jakolaskun a/b jäännös.
        */
    public static int mod(int a, int b) {
        int modulus = a % b;
        if(modulus < 0 ^ b < 0) {
            return modulus + b;
        } else {
            return modulus;
        }
    }
}