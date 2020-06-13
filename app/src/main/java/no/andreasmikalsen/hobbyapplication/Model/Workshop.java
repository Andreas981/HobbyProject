package no.andreasmikalsen.hobbyapplication.Model;

public class Workshop {
    String bedriftsNavn, adresse, postnummer, poststed;

    public Workshop(String bedriftsNavn, String adresse, String postnummer, String poststed) {
        this.bedriftsNavn = bedriftsNavn;
        this.adresse = adresse;
        this.postnummer = postnummer;
        this.poststed = poststed;
    }

    public String getBedriftsNavn() {
        return bedriftsNavn;
    }

    public void setBedriftsNavn(String bedriftsNavn) {
        this.bedriftsNavn = bedriftsNavn;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getPostnummer() {
        return postnummer;
    }

    public void setPostnummer(String postnummer) {
        this.postnummer = postnummer;
    }

    public String getPoststed() {
        return poststed;
    }

    public void setPoststed(String poststed) {
        this.poststed = poststed;
    }
}
