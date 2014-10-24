package es.unileon.happycow.abstractFactory;

import es.unileon.happycow.controller.InterfaceController;
import es.unileon.happycow.controller.ListFarmsController;
import es.unileon.happycow.gui.PanelListFarms;
import es.unileon.happycow.model.Farm;
import java.util.LinkedList;
import javax.swing.JPanel;

/**
 * Create the panel and controller that control the list window where a list of
 * farms is showed (enabled and disabled farms)
 * @author dorian
 */
public class FactoryListFarm implements FactoryWindows{
    /**
     * Concrete panel
     */
    private PanelListFarms listPanel;
    /**
     * Concrete controller
     */
    private ListFarmsController controller;
    /**
     * List of farm to show
     */
    private final LinkedList<Farm> list;

    
    /**
     * 
     * @param list list of farms to show
     */
    public FactoryListFarm(LinkedList<Farm> list) {
        this.list=list;
    }
    
    
    
    /**
     * 
     * @see es.unileon.happycow.abstractFactory.FactoryWindows#getController() 
     */
    @Override
    public InterfaceController getController() {
        if(controller==null){
            createController();
        }
        return controller;
    }

    /**
     * 
     * @see es.unileon.happycow.abstractFactory.FactoryWindows#getPanel() 
     */
    @Override
    public JPanel getPanel() {
        if(listPanel==null){
            createPanel();
        }
        return listPanel;
    }

    /**
     * @see es.unileon.happycow.abstractFactory.FactoryWindows#createController() 
     */
    @Override
    public void createController() {
        if(listPanel==null){
            createPanel();
        }
        
        if(controller==null){
            controller=new ListFarmsController(listPanel);
            //set the new controller to the panel
            listPanel.setController(controller);
        }
    }

    /**
     * 
     * @see es.unileon.happycow.abstractFactory.FactoryWindows#createPanel()
     */
    @Override
    public void createPanel() {
        if(listPanel==null){
                listPanel=new PanelListFarms(list);
        }
        
        //if controller exists, set the controller to the panel
        if(controller!=null){
            listPanel.setController(controller);
        }
    }

    @Override
    public void createElements() {
        createPanel();
        createController();
    }
    
}
