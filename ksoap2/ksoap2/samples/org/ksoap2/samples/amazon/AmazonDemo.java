package org.ksoap2.samples.amazon;

import java.util.Vector;

import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;

import org.ksoap2.*;
import org.ksoap2.serialization.*;
import org.ksoap2.transport.*;

public class AmazonDemo extends MIDlet implements CommandListener, Runnable {

	Display display;

    Form mainForm = new Form("Amazon Sample");
	TextField tagField = new TextField("Developer-Tag", "", 64, TextField.ANY);
    TextField symbolField = new TextField("Keyword", "pattern", 64, TextField.ANY);

	StringItem statusItem = new StringItem("Status", "idle");
	//TextField tagField = new TextField("")
//    StringItem resultItem = new StringItem("", "");
    static Command getCommand = new Command("Get", Command.SCREEN, 1);
	static Command detailCommand = new Command("Details", Command.SCREEN, 1);
	static Command newCommand = new Command("New", Command.SCREEN, 1);
	static Command backCommand = new Command("Back", Command.BACK, 1);
    Vector resultVector;
    List resultList;
    
    public AmazonDemo() {
		mainForm.append(tagField);
        mainForm.append(symbolField);
        mainForm.append(statusItem);
        mainForm.addCommand(getCommand);
        mainForm.setCommandListener(this);
    }

    public void startApp() {
        display = Display.getDisplay(this);
        display.setCurrent(mainForm);
    }

    public void pauseApp() {
    }

    public void destroyApp(boolean unconditional) {
    }

	
	public void run(){
		try {
			// build request string
			String symbol = symbolField.getString();

			statusItem.setText("building request");

			SoapObject rpc =
				new SoapObject("urn:PI/DevCentral/SoapService", "KeywordSearchRequest");

			
			SoapObject ro = new SoapObject("urn:PI/DevCentral/SoapService", "KeywordRequest");

			ro.addProperty("keyword", symbol.trim().toLowerCase());
			ro.addProperty("tag", "webservices-20");
			ro.addProperty("type", "lite");
			ro.addProperty("mode", "book");
			ro.addProperty("page", "1");
			ro.addProperty("devtag", tagField.getString()); 
			
			rpc.addProperty("KeywordSearchRequest", ro);
			
/*
			<keyword >dog</keyword>
											<page >1</page>
											<mode >book</mode>
											<tag >webservices-20</tag>
											<type >lite</type>
											<dev-tag >your-dev-tag</dev-tag>
											<format >xml</format>
											<version >1.0</version>
*/


			SoapSerializationEnvelope envelope =
				new SoapSerializationEnvelope(SoapEnvelope.VER11);

			envelope.bodyOut = rpc;

			//resultItem.setLabel(symbol);

			HttpTransport ht = new HttpTransport("http://soap.amazon.com/onca/soap3");
			 ht.debug = true;

			statusItem.setText("submitting request");

			try{

				ht.call(null, envelope);
				
				statusItem.setText("analyzing results...");

				System.err.println (ht.responseDump);  
				
				SoapObject result = (SoapObject) envelope.getResult();
				
			 	resultVector = (Vector) result.getProperty("Details"); //.getProperty("Details");
			 	
				
				resultList = new List("Result", List.IMPLICIT);
				resultList.addCommand(newCommand);
				resultList.addCommand(detailCommand);
				resultList.setCommandListener(this);
				
				for(int i = 0; i < resultVector.size(); i++){
					SoapObject detail = (SoapObject) resultVector.elementAt(i);
					resultList.append((String) detail.getProperty("ProductName"), null);
				}

				display.setCurrent(resultList);
				
//				for(int i = 0; i < result.getPropertyCount())
			 }
			 catch (SoapFault f) {
//				e.printStackTrace();
//				System.err.println (ht.requestDump);  
//				System.err.println (ht.responseDump);  

				statusItem.setText("Error (perhaps keyword not found): "+f.faultstring);
			 }
			 

		}
		catch (Exception e) {
			e.printStackTrace();
			statusItem.setText("Error: "+ e.toString());
		}
	
	}


    public void commandAction(Command c, Displayable d) {
    	if(c == getCommand){
			new Thread(this).start();
		}
		else if(c == newCommand){
			display.setCurrent(mainForm);
			statusItem.setText("idle");
		}
		else if(c == backCommand){
			display.setCurrent(resultList);
		}
		else {
			int sel = resultList.getSelectedIndex();
			SoapObject details = (SoapObject) resultVector.elementAt(sel);
			
			Form detailForm = new Form("Details: "+resultList.getString(sel));
			detailForm.setCommandListener(this);
			detailForm.addCommand(backCommand);
			PropertyInfo pi = new PropertyInfo();
			for(int i = 0; i < details.getPropertyCount(); i++) {
				details.getPropertyInfo(i, null, pi);
				if(pi.name.toLowerCase().indexOf("url")==-1)
					detailForm.append(new StringItem(pi.name, ""+details.getProperty(i)));
			}
			display.setCurrent(detailForm);
		}		
    }

/*
    public static void main(String[] argv) {
        new StockQuoteDemo().startApp();
    }*/
}
