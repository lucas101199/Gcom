package se.cs.umu.gcom.GUI;

public class GuiClient {

    public static void main(String[] args) {
        try {
            String name = "lucas";
            String name2 = "test2";
            String nameServerIp =  "192.168.1.2"; //"192.168.56.1";

            GUI_Gcom com_client = new GUI_Gcom(name,nameServerIp);
            //GUI_Gcom gui_gcom = new GUI_Gcom(name2,nameServerIp);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
