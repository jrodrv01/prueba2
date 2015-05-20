/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.unileon.happycow.database.concreteDatabase;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author dorian
 */
public class InsertCriterion {

    private final LinkedList<String> inserts;
    private Document xml;
    private final String pathXml = "CriterionsBase.xml";

    public InsertCriterion() {
        this.inserts = new LinkedList<String>();
    }

    private boolean run() {
        boolean result = true;
        if (getXml()) {
            getSentences();
        } else {
            result = false;
        }
        return result;
    }

    public LinkedList execute() {
        if(run()){
            return inserts;
        }else{
            return null;
        }
    }
    
    private File getTheFuckingXML(){
        InputStream inputStream = null;
	OutputStream outputStream = null;
        File theFuckingXML=new File(System.getProperty("java.io.tmpdir")+
                            System.getProperty("file.separator")+
                            "criterionsHappyCow.xml");
        try {
            
            theFuckingXML.createNewFile();
        } catch (IOException ex) {
            System.out.println(ex.toString());
        }
        System.out.println(theFuckingXML.exists());
        System.out.println(theFuckingXML.getAbsolutePath());
	try {
		// read this file into InputStream
		inputStream = getClass().getResourceAsStream("/criterions/CriterionsBase.xml");
 
		// write the inputStream to a FileOutputStream
		outputStream = 
                    new FileOutputStream(theFuckingXML);
 
		int read = 0;
		byte[] bytes = new byte[1024];
 
		while ((read = inputStream.read(bytes)) != -1) {
			outputStream.write(bytes, 0, read);
		}
 
		System.out.println("Done!");
                
                
 
	} catch (IOException e) {
		e.printStackTrace();
	} finally {
		if (inputStream != null) {
			try {
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (outputStream != null) {
			try {
				outputStream.flush();
				outputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
 
		}
                return theFuckingXML;
	}
    }


    private boolean getXml() {
        boolean result = true;
        try {
//            URI path=new URI(getClass().getResourceAsStream("/criterions/CriterionsBase.xml"));
//            String pathModified=path.getFile();
//            String pathFile=pathModified;
//            File file=new File(pathFile);
//            System.out.println(file.exists());
//            
//            System.out.println(getClass().getResource("/images/out.png"));
//            System.out.println(getClass().getResource("/criterions/CriterionsBase.xml"));
            
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            xml = docBuilder.parse(getTheFuckingXML());
        } catch (IOException ex) {
            System.out.println(ex.toString());
            result = false;
        } catch (ParserConfigurationException | SAXException ex) {
            result = false;
            System.out.println(ex.toString());
        }
        return result;
    }

    private void getSentences() {
        xml.getDocumentElement().normalize();
        System.out.println("Leyendo del nodo: " + xml.getDocumentElement().getNodeName());
        NodeList criterions = xml.getElementsByTagName("criterion");

        for (int i = 0; i < criterions.getLength(); i++) {
            StringBuilder aSentence = new StringBuilder();
            Node criterion = criterions.item(i);
            Element el=(Element)criterion;
            aSentence.append("INSERT INTO CRITERION (NOMBRECRITERIO,DESCRIPCION,HELP,CATEGORIA) VALUES ");
            aSentence.append("('");
            aSentence.append(getTagValue("name", el));
            aSentence.append("','");
            aSentence.append(getTagValue("description", el));
            aSentence.append("','");
            aSentence.append(getTagValue("help", el));
            aSentence.append("','");
            aSentence.append(getTagValue("category", el));
            aSentence.append("')");
            
            inserts.add(aSentence.toString());
        }

    }

    private String getTagValue(String tag, Element elemento) {
        NodeList lista = elemento.getElementsByTagName(tag).item(0).getChildNodes();
        Node valor = (Node) lista.item(0);
        return valor.getNodeValue();
    }

}
