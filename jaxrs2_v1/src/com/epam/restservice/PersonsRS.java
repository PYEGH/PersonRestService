package com.epam.restservice;

import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.servlet.ServletContext;

import com.fasterxml.jackson.databind.ObjectMapper;

@Path("/")
public class PersonsRS {
	@Context
	private ServletContext servletContext; // dependency injection
	private static PersonsList personsList; // set in getPersonList()

	private static final String JSON = "json";
	private static final String JSON_CONVERTATION_PROBLEM = "If you see this, there's a problem during convertation to json .";
	private static final String INCORRECT_ID = " is a n incorrect ID.\n";
	private static final String IO_FAILED_EXEPTION = "I/O failed!";
	private static final String FILE_NAME = "/WEB-INF/data/persons.db";
	private static final String FILE_DELIMETER = "!";
	private static final String PROPERTY_IS_MISSING = "One of properties (firstName, lastName, login, email) is missing.\n";
	private static final String CANNOT_DELETE = ". Cannot delete.\n";
	private static final String NO_PERSON_BY_ID = "There is no person with ID ";
	private static final String PERSON = "Person ";
	private static final String DELETED = " deleted.\n";
	private static final String UPDATED = " has been updated.\n";
	private static final String NO_PARAMS_PROVIDED = "No params is given: nothing to edit.\n";
	private static final String NO_PARAMS_PERSONS_FOUND = "There is no person with ID ";

	private static final String PARAM_ID = "id";
	private static final String PARAM_XML_TYPE = "application/xml";
	private static final String PARAM_JSON_TYPE = "application/json";
	private static final String PARAM_TEXT_TYPE = "text/plain";
	private static final String PARAM_FIRST_NAME = "firstName";
	private static final String PARAM_LAST_NAME = "lastName";
	private static final String PARAM_LOGIN = "login";
	private static final String PARAM_EMAIL = "email";

	public PersonsRS() {
	}

	@GET
	@Path("/xml")
	@Produces({ MediaType.APPLICATION_XML })
	public PersonsList getXml() {
		checkContext();
		return personsList;
	}

	@GET
	@Path("/xml/{id: \\d+}")
	@Produces({ MediaType.APPLICATION_XML })
	public Response getXml(@PathParam(PARAM_ID) int id) {
		checkContext();
		return toRequestedType(id, PARAM_XML_TYPE);
	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("/json")
	public Response getJson() {
		checkContext();
		return Response.ok(toJson(personsList), PARAM_JSON_TYPE).build();
	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("/json/{id: \\d+}")
	public Response getJson(@PathParam(PARAM_ID) int id) {
		checkContext();
		return toRequestedType(id, PARAM_JSON_TYPE);
	}

	@GET
	@Path("/plain")
	@Produces({ MediaType.TEXT_PLAIN })
	public String getPlain() {
		checkContext();
		return personsList.toString();
	}

	@POST
	@Produces({ MediaType.APPLICATION_XML })
	@Path("/create")
	public Response create(@FormParam(PARAM_FIRST_NAME) String firstName,
			@FormParam(PARAM_LAST_NAME) String lastName,
			@FormParam(PARAM_LOGIN) String login,
			@FormParam(PARAM_EMAIL) String email) {
		checkContext();
		if (firstName == null || lastName == null || login == null
				|| email == null) {
			return Response.status(Response.Status.BAD_REQUEST)
					.entity(PROPERTY_IS_MISSING).type(MediaType.TEXT_PLAIN)
					.build();
		}
		final int id = addPerson(firstName, lastName, login, email);
		return toRequestedType(id, PARAM_XML_TYPE);
	}

	@PUT
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("/update")
	public Response update(@FormParam(PARAM_ID) int id,
			@FormParam(PARAM_FIRST_NAME) String firstName,
			@FormParam(PARAM_LAST_NAME) String lastName,
			@FormParam(PARAM_LOGIN) String login,
			@FormParam(PARAM_EMAIL) String email) {

		checkContext();
		String msg = null;
		if (firstName == null && lastName == null && login == null
				&& email == null) {
			msg = NO_PARAMS_PROVIDED;
		}

		Person p = this.personsList.find(id);
		if (p == null) {
			msg = NO_PARAMS_PERSONS_FOUND + id + "\n";
		}

		if (msg != null)
			return Response.status(Response.Status.BAD_REQUEST).entity(msg)
					.type(MediaType.TEXT_PLAIN).build();

		if (firstName != null)
			p.setFirstName(firstName);
		if (lastName != null)
			p.setLastName(lastName);
		if (login != null)
			p.setLogin(login);
		if (email != null)
			p.setEmail(email);
		msg = PERSON + id + UPDATED;
		return toRequestedType(id, PARAM_JSON_TYPE);
	}

	@DELETE
	@Produces({ MediaType.TEXT_PLAIN })
	@Path("/delete/{id: \\d+}")
	public Response delete(@PathParam(PARAM_ID) int id) {
		checkContext();
		String msg = null;
		Person p = this.personsList.find(id);
		if (p == null) {
			msg = NO_PERSON_BY_ID + id + CANNOT_DELETE;
			return Response.status(Response.Status.BAD_REQUEST).entity(msg)
					.type(MediaType.TEXT_PLAIN).build();
		}
		this.personsList.getPersons().remove(p);
		msg = PERSON + id + DELETED;

		return Response.ok(msg, PARAM_TEXT_TYPE).build();
	}

	private void checkContext() {
		if (this.personsList == null) {
			getPersonList();
		}
	}

	/**
	 * Method get PersonList from File
	 */
	private void getPersonList() {
		this.personsList = new PersonsList();
		final InputStream in = this.servletContext
				.getResourceAsStream(FILE_NAME);

		if (in != null) {
			try {
				final BufferedReader reader = new BufferedReader(
						new InputStreamReader(in));
				String record = null;
				while ((record = reader.readLine()) != null) {
					String[] parts = record.split(FILE_DELIMETER);
					addPerson(parts[0], parts[1], parts[2], parts[3]);
				}
			} catch (Exception e) {
				throw new RuntimeException(IO_FAILED_EXEPTION);
			}
		}
	}

	/**
	 * Method adds person
	 * 
	 * @param firstName
	 * @param lastName
	 * @param login
	 * @param email
	 * @return
	 */
	private int addPerson(final String firstName, final String lastName,
			final String login, final String email) {
		final int id = this.personsList.add(firstName, lastName, login, email);
		return id;
	}

	/**
	 * Converts Person to JSON
	 * 
	 * @param personsList
	 * @return
	 */
	private String toJson(Person person) {
		String json;
		try {
			json = new ObjectMapper().writeValueAsString(person);
		} catch (Exception e) {
			throw new RuntimeException(IO_FAILED_EXEPTION);
		}
		return json;
	}

	/**
	 * Converts PersonList to JSON
	 * 
	 * @param personsList
	 * @return
	 */
	private String toJson(PersonsList personsList) {
		String json = null;
		try {
			json = new ObjectMapper().writeValueAsString(personsList);
		} catch (Exception e) {
			json = JSON_CONVERTATION_PROBLEM;
		}
		return json;
	}

	/**
	 * Method generate an HTTP error response or typed OK response.
	 * 
	 * @param id
	 * @param type
	 * @return
	 */
	private Response toRequestedType(int id, String type) {
		final Person person = this.personsList.find(id);
		if (person == null) {
			final String msg = id + INCORRECT_ID;
			return Response.status(Response.Status.BAD_REQUEST).entity(msg)
					.type(MediaType.TEXT_PLAIN).build();
		} else if (type.contains(JSON)) {
			return Response.ok(toJson(person), type).build();
		} else {
			return Response.ok(person, type).build();
		}
	}
}
