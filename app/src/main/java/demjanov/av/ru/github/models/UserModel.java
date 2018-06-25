package demjanov.av.ru.github.models;

public class UserModel {
    private byte[] userName;
    private byte[] userPass;


    /////////////////////////////////////////////////////
    // Constructors
    ////////////////////////////////////////////////////
    //-----Begin----------------------------------------
    public UserModel(){
        this.userName = new byte[0];
        this.userPass = new byte[0];
    }

    public UserModel(byte[] userName, byte[] userPass) {
        this.userName = userName;
        this.userPass = userPass;
    }
    //-----End------------------------------------------


    /////////////////////////////////////////////////////
    // Getters and Setters
    ////////////////////////////////////////////////////
    //-----Begin----------------------------------------

    public byte[] getUserName() {
        return userName;
    }

    public void setUserName(byte[] userName) {
        shredName();
        this.userName = userName;
    }

    public byte[] getUserPass() {
        return userPass;
    }

    public void setUserPass(byte[] userPass) {
        shredPass();
        this.userPass = userPass;
    }
    //-----End------------------------------------------


    /////////////////////////////////////////////////////
    // Methods shred
    ////////////////////////////////////////////////////
    //-----Begin----------------------------------------
    public void shred(){
        for(int i = 0; i < this.userName.length; i++){
            this.userName[i] >>>= 8;
        }

        for(int i = 0; i < this.userPass.length; i++){
            this.userPass[i] >>>= 8;
        }

        this.userName = new byte[0];
        this.userPass = new byte[0];
    }

    private void shredName(){
        for(int i = 0; i < this.userName.length; i++){
            this.userName[i] >>>= 8;
        }

        this.userName = new byte[0];
    }

    private void shredPass(){
        for(int i = 0; i < this.userPass.length; i++){
            this.userPass[i] >>>= 8;
        }

        this.userPass = new byte[0];
    }
    //-----End------------------------------------------

}
