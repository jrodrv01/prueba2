/*
 * 
 */
package es.unileon.happycow.controller.evaluation;

import es.unileon.happycow.application.Parameters;
import es.unileon.happycow.controller.IController;
import es.unileon.happycow.database.Database;
import es.unileon.happycow.gui.evaluation.PanelEvaluationCriterion;
import es.unileon.happycow.handler.Category;
import es.unileon.happycow.handler.IdCategory;
import es.unileon.happycow.handler.IdCriterion;
import es.unileon.happycow.handler.IdEvaluation;
import es.unileon.happycow.handler.IdFarm;
import es.unileon.happycow.handler.IdHandler;
import es.unileon.happycow.model.composite.Component;
import es.unileon.happycow.model.composite.Criterion;
import es.unileon.happycow.model.composite.Valoration;
import es.unileon.happycow.model.evaluation.EvaluationCriterionModel;
import es.unileon.happycow.strategy.EvaluationAlgorithm;
import java.awt.Color;
import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;
import javax.swing.JFileChooser;

/**
 *
 * @author dorian
 */
public class EvaluationCriterionController extends IController implements IEvaluationCriterionController {

    private EvaluationCriterionModel model;
    private PanelEvaluationCriterion gui;

    private Category actualCategory;
    private IdHandler actualCriterion;

    LinkedList<String>[] modelComboCriterion;

    private boolean newEvaluation;
    private int numberCows;

    /**
     * Caso 1, evaluación completamente nuevo, me llega o evaluación o
     * información Caso 2, evaluación a ver o modificar, me llega la evaluación
     * entera
     *
     * @param panel
     * @param model
     * @param newEvaluation
     */
    public EvaluationCriterionController(PanelEvaluationCriterion panel) {
        this.gui = panel;
        actualCategory = Category.FOOD;
        actualCriterion = null;

        initComboCriterion();
    }

    @Override
    public void onResume(Parameters parameters) {
        super.onResume(parameters);

        IdHandler farm = new IdFarm(Integer.parseInt(parameters.getString("idFarm")));
        boolean isNew = parameters.getBoolean("isNew");

        if (!isNew) {
            //rellenar los datos de evaluación
            IdHandler idEvaluation = new IdEvaluation(parameters.getInteger("idEvaluation"));
            model=new EvaluationCriterionModel(Database.getInstance().getEvaluation(idEvaluation));
        }else{
            model=new EvaluationCriterionModel(farm);
        }

        if (model != null) {
            numberCows = EvaluationAlgorithm.necesaryNumberOfCows(model.getInformation().getNumberCows());
            setNumberCows();
            addAll();
        }

        //establezco la categoría
        //cambio la lista del combo de criterios
        gui.setComboCriterion(modelComboCriterion[actualCategory.ordinal()]);
        //cambio la lista de criterios de la categoria
        LinkedList<Criterion> list=model.getListCriterion(actualCategory);
        LinkedList<String> listCriterions=new LinkedList<>();
        for (Criterion cri : list) {
            listCriterions.add(cri.getName());
        }
        gui.setCriterionList(listCriterions);

        //pongo la ponderacion de la categoria y su color correcto
        gui.setPonderationCategory(model.getWeighing(new IdCategory(actualCategory)));
        gui.setColorPonderationCategory(Color.BLACK);

        //si hay criterio establecido, establezco los datos
        if (actualCriterion == null) {
            gui.criterionInformationVisibility(false);
            gui.setValorationList(new LinkedList<Valoration>());

        } else {
            gui.criterionInformationVisibility(true);

            Criterion cri = model.getCriterion(actualCriterion);
            float ponderation = cri.getWeighing();
            boolean evaluated = gui.getCriterionEvaluated(actualCriterion);
            gui.setCriterionInformation(actualCriterion.toString(), ponderation, evaluated);

            gui.setValorationList(model.listOfCriterion(actualCriterion));
        }
    }

    private void addAll() {

    }

    private void setNumberCows() {
        gui.setTitleValorations("(mínimo ".concat(Integer.toString(numberCows)).concat(")"));
    }

    /**
     * Inicializo el contenido del combo de criterios que se añade
     */
    private void initComboCriterion() {
        int categories = Category.getArrayString().length;
        modelComboCriterion = new LinkedList[categories];
        for (int i = 0; i < categories; i++) {
            modelComboCriterion[i] = new LinkedList<>();
        }

        LinkedList<Criterion> lista = Database.getInstance().getListCriterion();
        for (Criterion criterion : lista) {
            IdCategory id = (IdCategory) criterion.getCategory();
            modelComboCriterion[Category.getEnum(id.toString()).ordinal()]
                    .add(criterion.getName());
        }
    }

    /**
     * Recibo la orden de descargar el fichero, hago lo necesario
     *
     * @param id
     */
    @Override
    public void downloadFile(IdHandler id) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int seleccion = fileChooser.showSaveDialog(null);

        if (seleccion == JFileChooser.APPROVE_OPTION) {
            File fichero = fileChooser.getSelectedFile();
            File downloaded = new File(fichero.getPath() + File.separator + id.toString());
//            System.out.println(downloaded.getAbsolutePath());
            byte[] data = Database.getInstance().getFile(model.getIdHandler(), id.toString());
            Database.getInstance().saveFileToTheSystem(data, downloaded);
        }
    }

    /**
     * Recibo la orden de borrar un fichero y lo borro en el modelo tengo que
     * notificar a la vista que quite ese fichero de la lista
     *
     * @param id
     */
    @Override
    public void removeFile(IdHandler id) {
        //borrar fichero del modelo
        //borro dicho fichero en la vista
        gui.removeFile(id);
    }

    @Override
    public void addFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int seleccion = fileChooser.showOpenDialog(null);
        if (seleccion == JFileChooser.APPROVE_OPTION) {
            File fichero = fileChooser.getSelectedFile();
            Database.getInstance().saveFile(model.getIdHandler(), fichero);
            //añado el fichero al gui
            gui.addFile(fichero.getName());
        }
    }

    @Override
    public void copyValoration(IdHandler id) {
        //lo duplico en el modelo
        Component valoration = model.getComposite().search(id);
        Valoration toClone = (Valoration) valoration;
        Valoration clone = new Valoration(toClone.getNota());
        model.add(toClone.getIdCategory(), toClone.getIdCriterion(), clone);

        //lo añado a la gui
        gui.addValoration(clone);
    }

    @Override
    public void removeValoration(IdHandler id) {
        //elimino la valoracion del modelo
        //pillo en que criterio y categoría está
        IdHandler idCategory = new IdCategory(actualCategory);
        IdHandler idCriterion = new IdCriterion(gui.getSelectedCriterion());
        model.removeValoration(idCategory, idCriterion, id);

        //y lo quito de la gui también
        gui.removeValoration(id);
    }

    /**
     * Me llega petición de añadir una nueva valoracion
     */
    @Override
    public void addNewValoration() {
        //lo añado al modelo
        IdHandler idCriterion = new IdCriterion(gui.getSelectedCriterion());
        Float note = gui.getSelectedValoration();
        model.add(new IdCategory(actualCategory), idCriterion, new Valoration(note));
        //notifico a la interfaz de que tiene que añadir una valoración a la lista
        gui.addValoration(null);
    }

    @Override
    public void criterionSelected() {
        if (actualCriterion==null || gui.getSelectedCriterion().compareTo(actualCriterion.toString()) != 0) {
            actualCriterion = new IdCriterion(gui.getSelectedCriterion());
            //change the criterion information
            Criterion cri = model.getCriterion(actualCriterion);
            float note = cri.getWeighing();
            boolean evaluated = gui.getCriterionEvaluated(actualCriterion);
            gui.setCriterionInformation(cri.getName(), note, evaluated);
            gui.setColorPonderationCriterion(Color.BLACK);

            //change the list valorations
            gui.setValorationList(model.listOfCriterion(actualCriterion));
        }
    }

    @Override
    public void addCriterions() {
        //añado la lista al modelo, y los voy añadiendo al gui
        List<Object> list = gui.getCriterionsAddingSelected();
        for (Object criterion : list) {
            Criterion cri = Database.getInstance().getCriterion(new IdCriterion((String) criterion));
            model.add(new IdCategory(actualCategory), cri);
            gui.addCriterion(cri.getId());
        }
    }

    @Override
    public void removeCriterion(IdHandler idCriterion) {
        //lo borro del modelo
        model.remove(idCriterion);
        //y lo quito del gui
        gui.removeCriterion(idCriterion);
    }

    @Override
    public void help(IdHandler id) {
        //mostrar información
    }

    @Override
    public void evaluated(IdHandler id) {
        //es parte del gui, en el modelo no hace nada
        boolean result = gui.getCriterionEvaluated(id);
        gui.setCriterionEvaluated(actualCriterion, !result);
    }

    @Override
    public void setPonderationCriterion(IdHandler id, String ponderation) {
        if (isFloatUnit(ponderation)) {
            //seteo en el modelo la ponderación
            float pon = Float.valueOf(ponderation);
            model.setWeighing(id, pon);
            gui.setColorPonderationCriterion(Color.BLACK);
        } else {
            gui.setColorPonderationCriterion(Color.RED);
        }
    }

    @Override
    public void setCategoryPonderation(String ponderation) {
        if (isFloatUnit(ponderation)) {
            //seteo en el modelo la ponderación
            float pon = Float.valueOf(ponderation);
            model.setWeighing(new IdCategory(actualCategory), pon);
            gui.setColorPonderationCategory(Color.BLACK);
        } else {
            gui.setColorPonderationCategory(Color.RED);
        }
    }

    private boolean isFloatUnit(String ponderation) {
        final String Digits = "(\\p{Digit}+)";
        final String HexDigits = "(\\p{XDigit}+)";
        // an exponent is 'e' or 'E' followed by an optionally 
        // signed decimal integer.
        final String Exp = "[eE][+-]?" + Digits;
        final String fpRegex
                = ("[\\x00-\\x20]*" + // Optional leading "whitespace"
                "[+-]?(" + // Optional sign character
                "NaN|" + // "NaN" string
                "Infinity|"
                + // "Infinity" string
                // A decimal floating-point string representing a finite positive
                // number without a leading sign has at most five basic pieces:
                // Digits . Digits ExponentPart FloatTypeSuffix
                // 
                // Since this method allows integer-only strings as input
                // in addition to strings of floating-point literals, the
                // two sub-patterns below are simplifications of the grammar
                // productions from the Java Language Specification, 2nd 
                // edition, section 3.10.2.
                // Digits ._opt Digits_opt ExponentPart_opt FloatTypeSuffix_opt
                "(((" + Digits + "(\\.)?(" + Digits + "?)(" + Exp + ")?)|"
                + // . Digits ExponentPart_opt FloatTypeSuffix_opt
                "(\\.(" + Digits + ")(" + Exp + ")?)|"
                + // Hexadecimal strings
                "(("
                + // 0[xX] HexDigits ._opt BinaryExponent FloatTypeSuffix_opt
                "(0[xX]" + HexDigits + "(\\.)?)|"
                + // 0[xX] HexDigits_opt . HexDigits BinaryExponent FloatTypeSuffix_opt
                "(0[xX]" + HexDigits + "?(\\.)" + HexDigits + ")"
                + ")[pP][+-]?" + Digits + "))"
                + "[fFdD]?))"
                + "[\\x00-\\x20]*");// Optional trailing "whitespace"

        return Pattern.matches(fpRegex, ponderation);
        //Double.valueOf(ponderation); // Will not throw NumberFormatException

    }

    @Override
    public void categorySelected(Category category) {
        if (actualCategory.compareTo(category) != 0) {
            actualCategory = category;
            //oculto la ventana que muestra información del criterio seleccionado
            gui.criterionInformationVisibility(false);

            //cambio la lista del combo de criterios
            gui.setCriterionList(modelComboCriterion[category.ordinal()]);

            //limpio el panel de valoraciones (con una lista vacía se limpia)
            gui.setValorationList(new LinkedList<Valoration>());

            //pongo la ponderacion de la categoria y su color correcto
            gui.setPonderationCategory(model.getWeighing(new IdCategory(category)));
            gui.setColorPonderationCategory(Color.BLACK);
        }
    }

    @Override
    public void finishEvaluation() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
