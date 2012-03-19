package net.wessendorf.j2me;


import javax.microedition.lcdui.*;
import javax.microedition.midlet.*;

import org.ksoap2.*;
import org.ksoap2.serialization.*;
import org.ksoap2.transport.*;



public class SoapDemo extends MIDlet implements CommandListener{

    private Display display;

    Form mainForm = new Form ("Hello World WebService");
    TextField nameField = new TextField ("Your name","",456,TextField.ANY);
    Command getCommand = new Command ("send", Command.SCREEN, 1);

    public SoapDemo () {
        mainForm.append (nameField);
        mainForm.addCommand (getCommand);
        mainForm.setCommandListener (this);
    }

    public void startApp() {
        display = Display.getDisplay (this);
        display.setCurrent (mainForm);
  }

    public void pauseApp() {
    }

    public void destroyApp(boolean unconditional) {
    }


    public void commandAction(Command c, Displayable s) {
        if (c == getCommand) {
            final TextBox t = new TextBox("", "", 256, 0);
            Thread thr = new Thread(){
                public void run() {
            try {

                SoapObject client = new
                SoapObject("","getObject");
                client.addProperty("name",nameField.getString());
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.bodyOut = client;
                HttpTransport ht = new
                HttpTransport("http://localhost:8080/axis/services/AxisService");
                ht.call("", envelope);

                SoapObject ret = new SoapObject("http://ws.wessendorf.net","CustomObject");
                ret = (SoapObject) envelope.getResponse();
                t.setString(ret.getProperty(0).toString());
            }
            catch ( SoapFault sf){
                String faultString = "Code: " + sf.faultcode + "\nString: " + sf.faultstring;
                t.setString(faultString);
            }
            catch ( Exception e){
                e.printStackTrace();
                t.setString(e.getMessage());
            }
            }};
            thr.start();
            display.setCurrent(t);
        }
        else{
            destroyApp(false);
            notifyDestroyed();
        }
    }
}
