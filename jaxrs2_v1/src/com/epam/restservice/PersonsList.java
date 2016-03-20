package com.epam.restservice;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "personsList")
public class PersonsList {
	private List<Person> persons;
	private AtomicInteger personId;

	public PersonsList() {
		persons = new CopyOnWriteArrayList<Person>();
		personId = new AtomicInteger();
	}

	@XmlElement
	@XmlElementWrapper(name = "persons")
	public List<Person> getPersons() {
		return this.persons;
	}

	public void setPersons(List<Person> persons) {
		this.persons = persons;
	}

	@Override
	public String toString() {
		final StringBuffer result = new StringBuffer("");
		for (Person person : this.persons) {
			result.append(person.toString());
		}
		return result.toString();
	}

	public Person find(int id) {
		Person person = null;
		for (Person currPerson : this.persons) {
			if (currPerson.getId() == id) {
				person = currPerson;
				break;
			}
		}
		return person;
	}

	public int add(final String firstName, final String lastName,
			final String login, final String email) {
		int id = this.personId.incrementAndGet();
		Person person = new Person();
		person.setFirstName(firstName);
		person.setLastName(lastName);
		person.setLogin(login);
		person.setEmail(email);
		person.setId(id);
		this.persons.add(person);
		return id;
	}
}