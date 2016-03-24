package com.vaadin.abiewska.view;

import java.util.List;

import com.vaadin.abiewska.domain.Course;
import com.vaadin.abiewska.domain.User;
import com.vaadin.abiewska.service.CourseManager;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItem;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class CreateCourseView extends VerticalLayout implements View {
	private Course courseSelect = null;
	private Object idSelect = null;

	@Override
	public void enter(ViewChangeEvent event) {
		this.addComponent(new MenuPanel());
		this.setUp();

	}

	public void setUp() {

		setMargin(true);
		setSpacing(true);
		Button btnAddCourse = new Button("Dodaj kurs");
		Button btnEditCourse = new Button("Edytuj kurs");
		Button btnRemoveCourse = new Button("Usuń");

		HorizontalLayout hButton = new HorizontalLayout();
		hButton.addComponents(btnAddCourse, btnRemoveCourse, btnEditCourse);
		hButton.setSpacing(true);

		BeanContainer<Integer, Course> courses = new BeanContainer<Integer, Course>(
				Course.class);

		courses.setBeanIdProperty("id");

		Table coursesTable = new Table("Kursy, które dodałeś", courses);
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
		coursesTable.setImmediate(true);

		List<Course> listCourse = null;
		User user = (User) UI.getCurrent().getSession()
				.getAttribute("currentUser");

		listCourse = CourseManager.getCoursesByLogin(user);

		courses.addAll(listCourse);
		coursesTable.setPageLength(coursesTable.size());

		btnAddCourse.addClickListener(e -> {
			AddCourseWindow addCourseWindow = new AddCourseWindow();
			UI.getCurrent().addWindow(addCourseWindow);
			addCourseWindow.addCloseListener(e1 -> {
				List<Course> list = null;
				list = CourseManager.getCoursesByLogin(user);
				courses.removeAllItems();
				courses.addAll(list);
				coursesTable.setPageLength(coursesTable.size());
			});
		});

		btnEditCourse.addClickListener(e -> {
			System.out.println(courseSelect);
			editCourse(courses, user, coursesTable);

		});

		coursesTable.addItemClickListener(e -> {
			BeanItem<Course> courseItem = courses.getItem(e.getItemId());
			idSelect = e.getItemId();
			courseSelect = courseItem.getBean();
			if (e.isDoubleClick()) {
				editCourse(courses, user, coursesTable);
			}

		});

		btnRemoveCourse.addClickListener(e -> {
			System.out.println(CreateCourseView.this.courseSelect.getId());
			if (CreateCourseView.this.courseSelect == null) {
				return;
			} else {
				CourseManager.removeCourse(courseSelect);
				courses.removeItem(idSelect);
				Notification
				.show("Usunąłeś kurs.",
						Notification.Type.HUMANIZED_MESSAGE);
				coursesTable.setPageLength(coursesTable.size());
			}

		});

		addComponents(coursesTable, hButton);
	}

	public void editCourse(BeanContainer<Integer, Course> courses, User user,
			Table coursesTable) {
		EditCourseWindow editCourseWindow = new EditCourseWindow(courseSelect);
		UI.getCurrent().addWindow(editCourseWindow);
		editCourseWindow.addCloseListener(e1 -> {
			List<Course> list = null;
			list = CourseManager.getCoursesByLogin(user);
			courses.removeAllItems();
			courses.addAll(list);
			coursesTable.setPageLength(coursesTable.size());
		});
	}
}

