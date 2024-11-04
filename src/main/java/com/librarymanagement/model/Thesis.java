package com.librarymanagement.model;

/**
 * Represents a Thesis, which is a specialized type of Document
 * with an academic advisor.
 */
public class Thesis extends Document {

    private String academicAdvisor;

    /**
     * Constructs a Thesis with the specified title, author, and academic advisor.
     *
     * @param title The title of the thesis.
     * @param author The author of the thesis.
     * @param academicAdvisor The academic advisor for the thesis.
     */
    public Thesis(String title, String author, String academicAdvisor) {
        super(title, author); // Call to Document's constructor
        this.academicAdvisor = academicAdvisor;
    }

    public Thesis(int id,String title,String author,String academicAdvisor){
        super(id,title,author);
        this.academicAdvisor=academicAdvisor;
    }

    /**
     * Gets the academic advisor for the thesis.
     *
     * @return The academic advisor.
     */
    public String getAcademicAdvisor() {
        return academicAdvisor;
    }
}
