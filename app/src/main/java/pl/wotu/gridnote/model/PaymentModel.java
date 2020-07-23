package pl.wotu.gridnote.model;

import android.graphics.drawable.Drawable;

import com.google.firebase.firestore.FieldValue;

import java.util.Date;

import java.text.ParseException;
import java.text.SimpleDateFormat;
//import java.util.Date;

public class PaymentModel {

    public static int PAYMENT_NEW = 0;
    public static int PAYMENT_REALISED = 1;
    public static int PAYMENT_NOT_REALISED = 2;

    private Date createTime;                //Data utworzenia
    private Date dateOfImplementation;      //Data realizacji
    private String title;                   //tytuł
    private String subtitle;                //podtytuł
    private String description;             //opis
    private double amount;                  //kwota
    private int numberOfAllInstallments;    // liczba wszystkich rat
    private int installmentNumber;          // numer raty
    private int status;                     // status -> zapłacone, niezapłacone...

    private String id;

    public PaymentModel() {
    }

    public PaymentModel(String id,String title, String subtitle, String description, double amount,Date createTime, Date dateOfImplementation,int installmentNumber,int numberOfAllInstallments) {
        this.createTime = createTime;
        this.dateOfImplementation = dateOfImplementation;
        this.title = title;
        this.subtitle = subtitle;
        this.description = description;
        this.amount = amount;
        this.installmentNumber = installmentNumber;
        this.numberOfAllInstallments = numberOfAllInstallments;

        this.status = PAYMENT_NEW;

//        this.hasDeadline = hasDeadline;
//        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
//        this.deadlineDate = format.parse ( deadlineDate );
//        this.realisedDate = format.parse ( realisedDate );
    }

    public PaymentModel(String id,String title, double amount, Date dateOfImplementation,Date createTime) {

        this.createTime = createTime;
        this.dateOfImplementation = dateOfImplementation;
        this.title = title;
        this.subtitle = "";
        this.description = "";
        this.amount = amount;
        this.installmentNumber = 0;
        this.numberOfAllInstallments = 0;

        this.status = PAYMENT_NEW;

//        this.hasDeadline = hasDeadline;
//        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
//        this.deadlineDate = format.parse ( deadlineDate );
//        this.realisedDate = format.parse ( realisedDate );
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getDateOfImplementation() {
        return dateOfImplementation;
    }

    public void setDateOfImplementation(Date dateOfImplementation) {
        this.dateOfImplementation = dateOfImplementation;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getID() {
        return id;
    }

    public int getNumberOfAllInstallments() {
        return numberOfAllInstallments;
    }

    public void setNumberOfAllInstallments(int numberOfAllInstallments) {
        this.numberOfAllInstallments = numberOfAllInstallments;
    }

    public int getInstallmentNumber() {
        return installmentNumber;
    }

    public void setInstallmentNumber(int installmentNumber) {
        this.installmentNumber = installmentNumber;
    }

//    public String getId() {
//        return id;
//    }
}
