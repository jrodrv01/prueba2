package es.unileon.happycow.model.composite;

import es.unileon.happycow.handler.IdHandler;

/**
 *
 * @author dorian
 */
public class Valoration extends Component {

    private float nota;
    
    public Valoration(IdHandler idHandler, float nota) {
        super();
        this.nota = nota;
        //por defecto el peso es 1
        this.entity=Entity.VALORATION;
        id = idHandler;
        weighing = 1;
    }

    public IdHandler getIdCategory() {
        //parent es el criterio, getparent me da la categoria
        return parent.getParent().getId();
    }

    public IdHandler getIdCriterion() {
        return this.parent.getId();
    }

    public float getNota() {
        return nota;
    }

    public void setNota(float nota) {
        this.nota=nota;
    }
    
    public IdHandler getIdEvaluation(){
        return getRoot().getId();
    }
    
    
    @Override
    public int size() {
        return 1;
    }

    @Override
    public Component search(IdHandler id) {
        if (this.id.compareTo(id) == 0) {
            return this;
        } else {
            return null;
        }
    }

    @Override
    public boolean remove(Component delete) {
        return false;
    }

    @Override
    public String toString() {
        return "\t\t\tValoration: " + this.id.getValue()+ "\n";
    }

}
