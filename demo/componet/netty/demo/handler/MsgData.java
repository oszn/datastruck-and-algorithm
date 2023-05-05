package demo.handler;

public class MsgData {
    String name;
    String msg;
    String data;
    int device;

    public MsgData(){

    };
    public MsgData(MsgData msgData){
        this.msg=msgData.getMsg();
        this.name=msgData.getName();
        this.data=msgData.getData();
        this.device=msgData.getDevice();
    }
    public int getDevice() {
        return device;
    }

    public void setDevice(int device) {
        this.device = device;
    }

    @Override
    public String toString() {
        return "MsgData{" +
                "name='" + name + '\'' +
                ", msg='" + msg + '\'' +
                ", data='" + data + '\'' +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
