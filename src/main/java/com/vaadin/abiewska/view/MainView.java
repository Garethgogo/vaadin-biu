package com.vaadin.abiewska.view;

import java.sql.SQLException;
import java.util.List;

import com.vaadin.abiewska.MyUI;
import com.vaadin.abiewska.domain.Course;
import com.vaadin.abiewska.domain.User;
import com.vaadin.abiewska.service.CourseManager;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItem;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class MainView extends VerticalLayout implements View {
	private Course courseSelect = null;

	@Override
	public void enter(ViewChangeEvent event) {
		this.addComponent(new MenuPanel());
		this.setUp();
	}

	public void setUp() {

		setMargin(true);
		setSpacing(true);

		final HorizontalLayout hLayout = new HorizontalLayout();

		Label lblLogin = new Label();
		Button btnSearch = new Button("Wyszukaj", FontAwesome.SEARCH);
		TextField txtCourse = new TextField();
		Label labelCourse = new Label("Nazwa kursu:");
		Button btnSelect = new Button("Zapisz się na kurs");
		
		User user = (User) UI.getCurrent().getSession()
				.getAttribute("currentUser");

		if (user != null) {
			lblLogin = new Label("Witaj, " + user.getLogin() + " ");

			setMargin(true);
			setSpacing(true);

		} else {
			UI.getCurrent().getNavigator().navigateTo("login");
		}	

		BeanContainer<Integer, Course> courses = new BeanContainer<Integer, Course>(
				Course.class);
		
		courses.setBeanIdProperty("id");

		Table coursesTable = new Table("Kursy", courses);
		coursesTable.setColumnHeader("id", "Id");
		coursesTable.setColumnHeader("name", "Nazwa");
		coursesTable.setColumnHeader("location", "Lokalizacja");
		coursesTable.setColumnHeader("description", "Szczegóły");
		coursesTable.setColumnHeader("email", "E-mail");
		coursesTable.setColumnHeader("dateBegin", "Data rozpoczęcia");
		coursesTable.setColumnHeader("dateEnd", "Data zakończenia");
		coursesTable.setImmediate(true);
		coursesTable.setSizeFull();
		coursesTable.setSelectable(true);
		coursesTable.setColumnWidth("description", 200);
		coursesTable.setVisibleColumns("id", "name", "location", "description",
				"email", "dateBegin", "dateEnd");

		txtCourse.addFocusListener(new FocusListener() {
			private static final long serialVersionUID = -6733373447805994139L;

			@Override
			public void focus(FocusEvent event) {

				btnSearch.setClickShortcut(KeyCode.ENTER);
			}
		});

		
		List<Course> listCourse = null;
		try {
			listCourse = CourseManager.getAllCourse();
		} catch (SQLException ex) {
			Notification.show("Brak połączenia z bazą.",
					Notification.Type.ERROR_MESSAGE);
		}

		courses.addAll(listCourse);
		coursesTable.setPageLength(coursesTable.size());


		btnSearch.addClickListener(e -> {
			String name = txtCourse.getValue();
			List<Course> list = null;
			try {
				list = CourseManager.getCourseByName(name);
			} catch (SQLException ex) {
				Notification.show("Brak połączenia z bazą.",
						Notification.Type.ERROR_MESSAGE);
			}

			courses.removeAllItems();
			courses.addAll(list);
			coursesTable.setPageLength(coursesTable.size());
			

		});

		coursesTable.addItemClickListener(e -> {

			BeanItem<Course> courseItem = courses.getItem(e.getItemId());
			courseSelect = courseItem.getBean();
			System.out.println("Zaznaczone" + e.getItem().toString());

		});

		btnSelect.addClickListener(e -> {
			if (MainView.this.courseSelect == null) {
				return;
			} else {
				System.out.println(MainView.this.courseSelect.getName());
				ConfirmWindow confrimWindow = new ConfirmWindow(
						MainView.this.courseSelect);
				UI.getCurrent().addWindow(confrimWindow);
			}

		});
		
		addComponent(lblLogin);
		hLayout.addComponents(labelCourse, txtCourse, btnSearch);
		hLayout.setMargin(true);
		hLayout.setSpacing(true);
		addComponent(hLayout);
		setComponentAlignment(hLayout, Alignment.MIDDLE_CENTER);
		addComponents(coursesTable,btnSelect);

	}

}
