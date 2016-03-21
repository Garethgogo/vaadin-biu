package com.vaadin.abiewska.domain;

import javax.validation.constraints.Size;

public class User {
	@Size(min = 3, max = 10, message = "Login od 3 do 10 znaków")
	private String login;

	@Size(min = 3, max = 10, message = "Hasło od 3 do 10 znaków")
	private String password;

	public User() {
		super();
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
