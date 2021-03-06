package com.theprogrammingturkey.customUI.client.gui;

import java.util.ArrayList;
import java.util.Arrays;

import com.theprogrammingturkey.customUI.config.CustomUIConfigLoader;
import com.theprogrammingturkey.customUI.config.CustomUISettings;
import com.theprogrammingturkey.customUI.util.CustomUIRenderer;
import com.theprogrammingturkey.gobblecore.client.gui.GuiSlider;
import com.theprogrammingturkey.gobblecore.client.gui.GuiToggleButton;
import com.theprogrammingturkey.gobblecore.util.MathUtil;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class ConfigGui extends GuiScreen
{
	private SettingEditing editing = SettingEditing.None;

	private GuiButton hitBoxSettings;
	private GuiButton guiOverlaySettings;
	private GuiButton buttonAnimationSettings;
	private GuiButton armorInfoSettings;

	private GuiScreen parentScreen;

	private GuiColorSelection boxOutlineColorSelelection;
	private GuiColorSelection boxFillColorSelelection;
	private GuiToggleButton customSelectionBox;
	private GuiButton blockSelectionColors;
	private GuiSlider thicknessSlider;
	private GuiToggleButton useDefaultBox;
	private GuiToggleButton highlightAffectedByLight;
	private GuiToggleButton highlightBlockFaces;

	private GuiColorSelection guiHighlightColorSelelection;
	private GuiToggleButton useGuiHighlight;

	private GuiToggleButton useButtonAnimation;
	private GuiButton buttonAnimationType;
	private GuiSlider animationSpeedSlider;

	private GuiToggleButton useArmorHUD;
	private GuiButton armorHUDPosition;
	private ItemStack[] testItems = { new ItemStack(Items.DIAMOND_BOOTS, 1), new ItemStack(Items.GOLDEN_LEGGINGS, 1), new ItemStack(Items.LEATHER_CHESTPLATE, 1), new ItemStack(Items.IRON_HELMET, 1), new ItemStack(Items.BOW, 1), new ItemStack(Items.STONE_SWORD, 1) };
	private boolean movingHUD = false;

	private boolean editingColor = false;

	public static boolean buttonPressedThisUpdate = false;
	private GuiSlider trackedSliderSelected = null;

	public ConfigGui(GuiScreen parent)
	{
		parentScreen = parent;
	}

	public void initGui()
	{
		this.buttonList.clear();
		this.buttonList.add(hitBoxSettings = new GuiButton(1000, this.width / 2 - 125, 25, 100, 20, "Block Selection"));
		this.buttonList.add(guiOverlaySettings = new GuiButton(1001, this.width / 2 + 25, 25, 100, 20, "Gui Slot Highlight"));
		this.buttonList.add(buttonAnimationSettings = new GuiButton(1002, this.width / 2 - 125, 50, 100, 20, "Button Animations"));
		this.buttonList.add(armorInfoSettings = new GuiButton(1003, this.width / 2 + 25, 50, 100, 20, "Armor Info"));

		this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height - 25, 200, 20, "Back"));

		boxOutlineColorSelelection = new GuiColorSelection("Block Highlight Outline", this.buttonList, this.width / 2 - 100, 30);
		boxOutlineColorSelelection.setCurrentValues(CustomUISettings.highlightColorR, CustomUISettings.highlightColorG, CustomUISettings.highlightColorB, CustomUISettings.highlightColorA);
		boxOutlineColorSelelection.setVisible(false);
		boxFillColorSelelection = new GuiColorSelection("Block Highlight Fill", this.buttonList, this.width / 2 - 100, 150);
		boxFillColorSelelection.setCurrentValues(CustomUISettings.fillColorR, CustomUISettings.fillColorG, CustomUISettings.fillColorB, CustomUISettings.fillColorA);
		boxFillColorSelelection.setVisible(false);

		this.buttonList.add(useDefaultBox = new GuiToggleButton(15, this.width / 2 - 100, 30, 150, 20, "Vanilla selection box: ", CustomUISettings.includeDefaultHighlight));
		this.buttonList.add(customSelectionBox = new GuiToggleButton(10, this.width / 2 - 100, 55, 150, 20, "Custom Selection Box: ", CustomUISettings.customHighlight));
		this.buttonList.add(highlightBlockFaces = new GuiToggleButton(17, this.width / 2 - 100, 80, 150, 20, "Highlight Block Faces: ", CustomUISettings.highlightBlockFaces));
		this.buttonList.add(blockSelectionColors = new GuiButton(11, this.width / 2 - 100, 105, 150, 20, "Set Colors"));
		this.buttonList.add(thicknessSlider = new GuiSlider(14, "Thickness", this.width / 2 - 100, 130, 1F, 10F, CustomUISettings.highlightLineThickness, 0.5F));
		this.buttonList.add(highlightAffectedByLight = new GuiToggleButton(16, this.width / 2 - 100, 155, 150, 20, "Dim with light levels: ", CustomUISettings.highlightAffectedByLight));

		guiHighlightColorSelelection = new GuiColorSelection("Gui Highligt", this.buttonList, this.width / 2 - 100, 30);
		guiHighlightColorSelelection.setCurrentValues(CustomUISettings.guihighlightColorR, CustomUISettings.guihighlightColorG, CustomUISettings.guihighlightColorB, CustomUISettings.guihighlightColorA);
		this.buttonList.add(useGuiHighlight = new GuiToggleButton(44, this.width / 2 - 100, 150, 150, 20, "Gui Highlight: ", CustomUISettings.guiHighlight));

		this.buttonList.add(useButtonAnimation = new GuiToggleButton(20, this.width / 2 - 100, 30, 200, 20, "Button Animations: ", CustomUISettings.buttonAnimation));
		this.buttonList.add(buttonAnimationType = new GuiButton(21, this.width / 2 - 100, 55, 200, 20, "Button Animation Type: " + CustomUISettings.buttonAnimationType.getTypeName()));
		this.buttonList.add(animationSpeedSlider = new GuiSlider(22, "Speed", this.width / 2 - 75, 80, 0F, 40F, CustomUISettings.buttonAnimationSpeed, 1F));

		this.buttonList.add(useArmorHUD = new GuiToggleButton(30, this.width / 2 - 100, 30, 200, 20, "Armor Gui Hud: ", CustomUISettings.armorGuiHud));
		this.buttonList.add(armorHUDPosition = new GuiButton(31, this.width / 2 - 100, 55, 200, 20, "Change HUD Position"));

		this.setEditState(editing);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{
		this.drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);

		if(this.editing == SettingEditing.BlockHighlight && editingColor)
		{
			this.boxOutlineColorSelelection.drawScreen(mouseX, mouseY, partialTicks);
			this.boxFillColorSelelection.drawScreen(mouseX, mouseY, partialTicks);
		}
		else if(this.editing == SettingEditing.GuiHighlight)
		{
			this.guiHighlightColorSelelection.drawScreen(mouseX, mouseY, partialTicks);
		}

		if(movingHUD)
		{
			CustomUIRenderer.renderDurabilityHUD(mc, new ArrayList<ItemStack>(Arrays.asList(this.testItems)));
		}
	}

	protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick)
	{
		if(movingHUD)
		{
			ScaledResolution scaledresolution = new ScaledResolution(mc);
			CustomUISettings.armorGuiHudX = MathUtil.clamp(0f, 1f, (float) (mouseX - 45) / (float) scaledresolution.getScaledWidth());
			CustomUISettings.armorGuiHudY = MathUtil.clamp(0f, 1f, (float) (scaledresolution.getScaledHeight() - mouseY - 50) / (float) scaledresolution.getScaledHeight());
		}
	}

	public void updateScreen()
	{
		buttonPressedThisUpdate = false;
	}

	protected void actionPerformed(GuiButton button)
	{
		if(buttonPressedThisUpdate)
			return;

		buttonPressedThisUpdate = true;

		if(button.enabled)
		{
			if(button.id == 0)
			{

				boolean goBack = false;
				if(this.editing == SettingEditing.None)
				{
					this.mc.displayGuiScreen(this.parentScreen);
				}
				else if(this.editing == SettingEditing.BlockHighlight)
				{
					if(this.editingColor)
					{
						this.editingColor = false;
						this.boxFillColorSelelection.setVisible(false);
						this.boxOutlineColorSelelection.setVisible(false);
						this.customSelectionBox.visible = true;
						this.blockSelectionColors.visible = true;
						this.thicknessSlider.visible = true;
						this.useDefaultBox.visible = true;
						this.highlightAffectedByLight.visible = true;
						this.highlightBlockFaces.visible = true;
					}
					else
					{
						goBack = true;
					}

					CustomUIConfigLoader.saveBlockHighlightSettings(this.boxOutlineColorSelelection, this.boxFillColorSelelection, thicknessSlider.getValueAdjusted(10.0F));
				}
				else if(this.editing == SettingEditing.GuiHighlight)
				{
					CustomUIConfigLoader.saveGuiHighlightSettings(this.guiHighlightColorSelelection);
					goBack = true;
				}
				else if(this.editing == SettingEditing.ButtonAnimation)
				{
					CustomUIConfigLoader.saveButtonAnimationSettings(this.animationSpeedSlider.getValueAdjusted(40.0F), CustomUISettings.buttonAnimationType);
					goBack = true;
				}
				else if(this.editing == SettingEditing.ArmorInfo)
				{
					if(this.movingHUD)
					{
						this.useArmorHUD.visible = true;
						this.armorHUDPosition.visible = true;
						this.movingHUD = false;
					}
					else
					{
						CustomUIConfigLoader.saveArmorInfoSettings();
						goBack = true;
					}
				}

				if(goBack)
					this.setEditState(SettingEditing.None);
			}
			else if(button.id == 10)
			{
				CustomUISettings.customHighlight = this.customSelectionBox.isToggledOn();
			}
			else if(button.id == 11)
			{
				this.editingColor = true;
				this.boxFillColorSelelection.setVisible(true);
				this.boxOutlineColorSelelection.setVisible(true);
				this.customSelectionBox.visible = false;
				this.blockSelectionColors.visible = false;
				this.thicknessSlider.visible = false;
				this.useDefaultBox.visible = false;
				this.highlightAffectedByLight.visible = false;
				this.highlightBlockFaces.visible = false;
			}
			else if(button.id == 15)
			{
				CustomUISettings.includeDefaultHighlight = this.useDefaultBox.isToggledOn();
			}
			else if(button.id == 16)
			{
				CustomUISettings.highlightAffectedByLight = this.highlightAffectedByLight.isToggledOn();
			}
			else if(button.id == 17)
			{
				CustomUISettings.highlightBlockFaces = this.highlightBlockFaces.isToggledOn();
			}
			else if(button.id == 20)
			{
				CustomUISettings.buttonAnimation = this.useButtonAnimation.isToggledOn();
			}
			else if(button.id == 21)
			{
				CustomUISettings.buttonAnimationType = CustomUISettings.buttonAnimationType.getNext();
				this.buttonAnimationType.displayString = "Button Animation Type: " + CustomUISettings.buttonAnimationType.getTypeName();
			}
			else if(button.id == 22)
			{
				trackedSliderSelected = this.animationSpeedSlider;
			}
			else if(button.id == 30)
			{
				CustomUISettings.armorGuiHud = this.useArmorHUD.isToggledOn();
			}
			else if(button.id == 31)
			{
				this.useArmorHUD.visible = false;
				this.armorHUDPosition.visible = false;
				this.movingHUD = true;
			}
			else if(button.id == 44)
			{
				CustomUISettings.guiHighlight = this.useGuiHighlight.isToggledOn();
			}
			else if(button.id == 1000)
			{
				this.setEditState(SettingEditing.BlockHighlight);
			}
			else if(button.id == 1001)
			{
				this.setEditState(SettingEditing.GuiHighlight);
			}
			else if(button.id == 1002)
			{
				this.setEditState(SettingEditing.ButtonAnimation);
			}
			else if(button.id == 1003)
			{
				this.setEditState(SettingEditing.ArmorInfo);
			}
		}
	}

	protected void mouseReleased(int mouseX, int mouseY, int state)
	{
		super.mouseReleased(mouseX, mouseY, state);
		if(trackedSliderSelected != null)
		{
			if(trackedSliderSelected.id == 22)
			{
				CustomUISettings.buttonAnimationSpeed = this.animationSpeedSlider.getValueAdjusted(40.0F);
				trackedSliderSelected = null;
			}
		}
	}

	public void setEditState(SettingEditing setting)
	{
		this.customSelectionBox.visible = setting == SettingEditing.BlockHighlight;
		this.blockSelectionColors.visible = setting == SettingEditing.BlockHighlight;
		this.guiHighlightColorSelelection.setVisible(setting == SettingEditing.GuiHighlight);
		this.thicknessSlider.visible = setting == SettingEditing.BlockHighlight;
		this.useDefaultBox.visible = setting == SettingEditing.BlockHighlight;
		this.highlightAffectedByLight.visible = setting == SettingEditing.BlockHighlight;
		this.highlightBlockFaces.visible = setting == SettingEditing.BlockHighlight;
		this.useGuiHighlight.visible = setting == SettingEditing.GuiHighlight;
		this.hitBoxSettings.visible = setting == SettingEditing.None;
		this.guiOverlaySettings.visible = setting == SettingEditing.None;
		this.buttonAnimationSettings.visible = setting == SettingEditing.None;
		this.armorInfoSettings.visible = setting == SettingEditing.None;
		this.useButtonAnimation.visible = setting == SettingEditing.ButtonAnimation;
		this.buttonAnimationType.visible = setting == SettingEditing.ButtonAnimation;
		this.animationSpeedSlider.visible = setting == SettingEditing.ButtonAnimation;
		this.useArmorHUD.visible = setting == SettingEditing.ArmorInfo;
		this.armorHUDPosition.visible = setting == SettingEditing.ArmorInfo;

		this.editing = setting;
	}

	public enum SettingEditing
	{
		None, BlockHighlight, GuiHighlight, ButtonAnimation, ArmorInfo;
	}

	public enum ButtonAnimationType
	{
		None("None"), SlideUp("Slide Up"), SlideRight("Slide Right"), SlideLeft("Slide Left"), SlideIn("Slide In"), FadeIn("Fade In");

		private String typeName;

		ButtonAnimationType(String name)
		{
			this.typeName = name;
		}

		public String getTypeName()
		{
			return this.typeName;
		}

		public ButtonAnimationType getNext()
		{
			switch(this)
			{
				case None:
					return SlideUp;
				case SlideUp:
					return SlideLeft;
				case SlideLeft:
					return SlideRight;
				case SlideRight:
					return SlideIn;
				case SlideIn:
					return FadeIn;
				case FadeIn:
					return None;
			}
			return None;
		}

		public static ButtonAnimationType getTypeFromName(String name)
		{
			for(ButtonAnimationType type : values())
				if(type.getTypeName().equalsIgnoreCase(name))
					return type;
			return None;
		}
	}
}