/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.edu.bsi.biblioteca;

/**
 *
 * @author Carlos
 */
public class AutorItem {

    private int id;
    private String nome;

    public AutorItem(int id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return nome; // é isso que aparece no ComboBox
    }

}
