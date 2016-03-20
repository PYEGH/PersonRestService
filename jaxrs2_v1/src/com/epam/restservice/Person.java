package com.epam.restservice;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlElement;

@XmlRootElement(name = "person")
public class Person {
	private String firstName;
	private String lastName;
	private String login;
	private String email;
	private int id;

	public Person() {
	}

	@Override
	public String toString() {
		return this.firstName + " " + this.lastName + " " + this.login + " "
				+ this.email + "\n";
	}

	@XmlElement
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	@XmlElement
	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@XmlElement
	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	@XmlElement
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setId(int id) {
		this.id = id;
	}

	@XmlElement
	public int getId() {
		return this.id;
	}
}