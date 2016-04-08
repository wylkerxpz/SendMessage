package inf.ufg.br.sendmessage;

public class Disciplina {

    int disciplina_pk = 0;
    String disciplina_nome = null;
    boolean selected = false;

    public Disciplina(int disciplina_pk, String disciplina_nome, boolean selected) {
        super();
        this.disciplina_pk = disciplina_pk;
        this.disciplina_nome = disciplina_nome;
        this.selected = selected;
    }

    public int getDisciplina_pk() {
        return disciplina_pk;
    }

    public String getDisciplina_nome() {
        return disciplina_nome;
    }

    public void setDisciplina_pk(int disciplina_pk) {
        this.disciplina_pk = disciplina_pk;
    }

    public void setDisciplina_nome(String disciplina_nome) {
        this.disciplina_nome = disciplina_nome;
    }

    public boolean isSelected() {
        return selected;
    }
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

}
