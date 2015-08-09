package es.unileon.happycow.application.windows;

import es.unileon.happycow.factory.Factory;
import es.unileon.happycow.handler.IdWindow;

/**
 *
 * @author dorian
 */
public class Administrator extends IWindow{
    public static Window TYPE=Window.ADMINISTRATION;

    public Administrator(Factory factory) {
        super("Administración",true, true, new IdWindow(TYPE, false), factory);
    } 
    
    @Override
    public Window getType() {
        return TYPE;
    }
}