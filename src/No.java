public class No {
    private int valor;
    private No direita;
    private No esquerda;
    private int balanco;

    public No(int valor){
        this.valor = valor;
        this.direita = null;
        this.esquerda = null;
        this.balanco = 0;
    }
    public No(No direita, int valor, No esquerda) {
        this.direita = direita;
        this.valor = valor;
        this.esquerda = esquerda;

    }

    public int getValor() {
        return valor;
    }

    public void setValor(int valor) {
        this.valor = valor;
    }

    public No getDireita() {
        return direita;
    }

    public void setDireita(No direita) {
        this.direita = direita;
    }

    public No getEsquerda() {
        return esquerda;
    }

    public void setEsquerda(No esquerda) {
        this.esquerda = esquerda;
    }

    public int getAltura() {
        return calcularAltura(this);
    }

    public String getTipo() {
        if (isFolha()) return "Folha";
        return "Interno";
    }

    public int getBalanco() {
        return balanco;
    }

    public void setBalanco(No no) {
        calcularBalanco(no);
    }

    private int calcularAltura(No no) {
        if (no == null) return -1;
        return 1 + Math.max(calcularAltura(no.getEsquerda()), calcularAltura(no.getDireita()));
    }
    private int calcularBalanco(No no){
        return calcularAltura(no.getEsquerda()) - calcularAltura(no.getDireita());
    }

    public boolean isFolha() {
        return esquerda == null && direita == null;
    }


}
