package org.ksoap2.samples.quotes;

import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;

import org.ksoap2.*;
import org.ksoap2.serialization.*;
import org.ksoap2.transport.*;

public class StockQuoteDemo extends MIDlet implements CommandListener, Runnable {

    Form mainForm = new Form("StockQuotes");
    TextField symbolField = new TextField("Symbol", "IBM", 5, TextField.ANY);
    StringItem resultItem = new StringItem("", "");
    Command getCommand = new Command("Get", Command.SCREEN, 1);

    public StockQuoteDemo() {
        mainForm.append(symbolField);
        mainForm.append(resultItem);
        mainForm.addCommand(getCommand);
        mainForm.setCommandListener(this);
    }

    public void startApp() {
        Display.getDisplay(this).setCurrent(mainForm);
    }

    public void pauseApp() {
    }

    public void destroyApp(boolean unconditional) {
    }

	
	public void run(){
		try {
			// build request string
			String symbol = symbolField.getString();

			SoapObject rpc =
				new SoapObject("urn:xmethods-delayed-quotes", "getQuote");

			rpc.addProperty("symbol", symbol);

			SoapSerializationEnvelope envelope =
				new SoapSerializationEnvelope(SoapEnvelope.VER10);

			envelope.bodyOut = rpc;

			resultItem.setLabel(symbol);

			HttpTransport ht = new HttpTransport("http://services.xmethods.net/soap");
			//ht.debug = true;
             
			ht.call("urn:xmethods-delayed-quotes#getQuote", envelope);
			resultItem.setText("" + envelope.getResult());
		}
		catch (Exception e) {
			e.printStackTrace();
			resultItem.setLabel("Error:");
			resultItem.setText(e.toString());
		}
	
	}


    public void commandAction(Command c, Displayable d) {
		new Thread(this).start();
    }

    /** for me4se */

    public static void main(String[] argv) {
        new StockQuoteDemo().startApp();
    }
}
