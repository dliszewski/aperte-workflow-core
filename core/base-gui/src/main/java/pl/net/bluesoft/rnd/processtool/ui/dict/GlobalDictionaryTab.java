package pl.net.bluesoft.rnd.processtool.ui.dict;

import com.vaadin.terminal.Sizeable;
import org.aperteworkflow.util.vaadin.VaadinUtility;

import pl.net.bluesoft.rnd.processtool.model.dict.db.ProcessDBDictionary;
import pl.net.bluesoft.rnd.processtool.model.dict.db.ProcessDBDictionaryItem;
import pl.net.bluesoft.rnd.processtool.ui.dict.modelview.GlobalDictionaryModelView;
import pl.net.bluesoft.rnd.processtool.ui.dict.request.AddNewDictionaryItemActionRequest;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Select;

/**
 * Tab component for global dicionaries
 * 
 * @author mpawlak@bluesoft.net.pl
 *
 */
public final class GlobalDictionaryTab extends DictionaryTab implements ValueChangeListener
{
	private Select selectDictionary;
	private Select selectLocale;
	
	private Button addButton;
	
	public GlobalDictionaryTab(DictionariesMainPane mainPanel, GlobalDictionaryModelView modelView) {
		super(mainPanel, modelView);
		init();
	}
	
	@Override
	protected void init() 
	{
    	super.init();
       	
    	selectDictionary = new Select();
    	selectDictionary.setImmediate(true);
    	selectDictionary.setVisible(false);
    	selectDictionary.setNullSelectionAllowed(false);

    	selectLocale = new Select();
    	selectLocale.setImmediate(true);
    	selectLocale.setVisible(true);
    	selectLocale.setNullSelectionAllowed(false);
		selectLocale.setNewItemsAllowed(true);
		selectLocale.setWidth(100, Sizeable.UNITS_PIXELS);
    	
    	selectDictionary.addListener((ValueChangeListener)this);
    	selectLocale.addListener((ValueChangeListener)this);
    	
        addButton = VaadinUtility.addIcon(mainPanel.getApplication());
        addButton.setCaption(getMessage("dict.addentry"));
        addButton.setVisible(false);
        addButton.addListener((ClickListener)this);
        addButton.setDisableOnClick(true);
    	
    	headerLayout.addComponent(selectLocale);
    	headerLayout.addComponent(selectDictionary);
    	headerLayout.addComponent(addButton);
    	
    	dictionaryItemTable.setContainerDataSource(getGlobalModelView().getBeanItemContainerDictionaryItems());
    	
        selectDictionary.setContainerDataSource(getGlobalModelView().getBeanItemContainerDictionaries());
        selectLocale.setContainerDataSource(getGlobalModelView().getBeanItemContainerLanguageCodes());
	}
	
	@Override
	protected void disableEdition() 
	{
		addButton.setEnabled(true);
		
		super.disableEdition();
	}

    @Override
	public void loadData()
    {
    	getGlobalModelView().reloadData();
    }

    public GlobalDictionaryModelView getGlobalModelView() {
		return (GlobalDictionaryModelView)getModelView();
	}
    
	@Override
	public void valueChange(ValueChangeEvent event) 
	{
		/* Locale selected, show items */
		if(event.getProperty().equals(selectLocale))
		{		
			/* Disable dictionary item edition */
			dicardChanges();
			
			getGlobalModelView().setSelectedLocale((String)selectLocale.getValue());
			
			/* Selecting new locale changes selected dictionary */
			ProcessDBDictionary dictionaryInNewLocale = getGlobalModelView().getSelectedDictionary();
			selectDictionary.select(dictionaryInNewLocale);
			
			selectDictionary.setVisible(true);
			
		}
		/* Dictionary selected, filter locals by this dictionary */
		else if(event.getProperty().equals(selectDictionary))
		{
			/* Disable dictionary item edition */
			dicardChanges();
			
			ProcessDBDictionary selectedDictionary = (ProcessDBDictionary)selectDictionary.getValue();
			getGlobalModelView().setSelectedDictionary(selectedDictionary);
			refreshData();
			dictionaryItemTable.sort();

			addButton.setVisible(selectedDictionary != null);
		}
	}
	
	@Override
	public void buttonClick(ClickEvent event)  
	{
		if(event.getButton().equals(addButton))
		{
			ProcessDBDictionaryItem item = new ProcessDBDictionaryItem();
			ProcessDBDictionary selectedDictionary = (ProcessDBDictionary)selectDictionary.getValue();
			
			if(selectedDictionary == null)
				return;
			
			item.setDictionary(selectedDictionary);
			
			AddNewDictionaryItemActionRequest addNewActionReaquest = new AddNewDictionaryItemActionRequest(item);
			mainPanel.handleActionRequest(addNewActionReaquest);
		}
		else
			super.buttonClick(event);
		
	}
}