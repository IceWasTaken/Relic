package net.ice.artifact.widget;

import io.qt.widgets.QPushButton;
import io.qt.widgets.QWidget;
import io.qt.widgets.QWidgetAction;

public abstract class Button {

	private final String text;
	private final String objName;

	public abstract void onPress();

	public Button(String text, String objName) {
		this.text = text;
		this.objName = objName;
	}

	public Button draw(QWidget widget) {
		QPushButton button = new QPushButton(text, widget);
		QWidgetAction action = new QWidgetAction(widget);
		button.clicked.connect(this::onPress);
		button.setObjectName(objName);
		action.setDefaultWidget(button);
		widget.addAction(action);

		return this;
	}
}
