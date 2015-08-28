/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.unileon.happycow.controller.admin;

import es.unileon.happycow.controller.Controller;
import es.unileon.happycow.gui.admin.InterfaceTabs;
import java.util.LinkedList;

/**
 *
 * @author dorian
 */
public class AdminController extends Controller {
    
    LinkedList<InterfaceTabs> panels;

    public AdminController() {
        panels=new LinkedList<>();
    }
    
    public void addTab(InterfaceTabs panel){
        panels.add(panel);
    }
    
    public void changeTab(int index){
        panels.get(index).updateInformation();
    }

    
}
