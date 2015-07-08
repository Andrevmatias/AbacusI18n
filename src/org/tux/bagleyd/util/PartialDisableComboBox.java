package org.tux.bagleyd.util;

// http://vetruvet.blogspot.com/2011/03/jcombobox-with-disabled-items.html
// Valera

import java.awt.Component;
import java.util.ArrayList;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

public class PartialDisableComboBox extends JComboBox<Object> {
	private static final long serialVersionUID = -1690671707274328126L;
	
	private ArrayList<Boolean> itemsState = new ArrayList<>();
	@SuppressWarnings("unchecked")
	public PartialDisableComboBox() {
		super();
		this.setRenderer(new BasicComboBoxRenderer() {
			private static final long serialVersionUID = -2774241371293899669L;
			@Override
			public Component getListCellRendererComponent(final JList list,
					final Object value, final int index,
					final boolean isSelected, final boolean cellHasFocus) {
				//Component
				final Component c = super.getListCellRendererComponent(list,
					value, index, isSelected, cellHasFocus);
				final boolean disabled = (index >= 0 &&
					index < getItemsState().size() &&
					!getItemsState().get(index));
				c.setEnabled(!disabled);
				c.setFocusable(!disabled);
				return c;
			}
		});
	}
	
	@Override
	public void addItem(Object item) {
		this.addItem(item, true);
	}
	
	public void addItem(Object item, boolean enabled) {
		super.addItem(item);
		getItemsState().add(enabled);
	}
	
	@Override
	public void insertItemAt(Object item, int index) {
		this.insertItemAt(item, index, true);
	}

	public void insertItemAt(Object item, int index, boolean enabled) {
		super.insertItemAt(item, index);
		getItemsState().add(index, enabled);
	}
	
	@Override
	public void removeAllItems() {
		super.removeAllItems();
		getItemsState().clear();
	}
	
	@Override
	public void removeItemAt(int index) {
		if (index < 0 || index >= getItemsState().size())
			throw new IllegalArgumentException("Item Index out of Bounds!");
		super.removeItemAt(index);
		getItemsState().remove(index);
	}
	
	@Override
	public void removeItem(Object item) {
		for (int q = 0; q < this.getItemCount(); q++) {
			if (this.getItemAt(q) == item)
				getItemsState().remove(q);
		}
		super.removeItem(item);
	}
	
	@Override
	public void setSelectedIndex(int index) {
		if (index < 0 || index >= getItemsState().size())
			throw new IllegalArgumentException("Item Index out of Bounds!");
		if (getItemsState().get(index))
			super.setSelectedIndex(index);
	}
	
	public void setItemEnabled(int index, boolean enabled) {
		if (index < 0 || index >= getItemsState().size())
			throw new IllegalArgumentException("Item Index out of Bounds!");
		getItemsState().set(index, enabled);
	}
	
	public boolean isItemEnabled(int index) {
		if (index < 0 || index >= getItemsState().size())
			throw new IllegalArgumentException("Item Index out of Bounds!");
		return getItemsState().get(index);
	}

	public ArrayList<Boolean> getItemsState() {
		return itemsState;
	}

	public void setItemsState(ArrayList<Boolean> itemsState) {
		this.itemsState = itemsState;
	}
}
