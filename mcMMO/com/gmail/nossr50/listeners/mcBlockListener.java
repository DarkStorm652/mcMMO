package com.gmail.nossr50.listeners;

import com.gmail.nossr50.Users;
import com.gmail.nossr50.m;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.mcPermissions;
import com.gmail.nossr50.config.LoadProperties;
import com.gmail.nossr50.contrib.contribStuff;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;

import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkitcontrib.player.ContribCraftPlayer;
import org.bukkitcontrib.player.ContribPlayer;
import org.bukkitcontrib.sound.SoundEffect;

import com.gmail.nossr50.locale.mcLocale;
import com.gmail.nossr50.skills.*;
import com.gmail.nossr50.datatypes.FakeBlockBreakEvent;


public class mcBlockListener extends BlockListener {
    private final mcMMO plugin;

    public mcBlockListener(final mcMMO plugin) {
        this.plugin = plugin;
    }
    
    public void onBlockPlace(BlockPlaceEvent event) 
    {
    	
    	Block block;
    	Player player = event.getPlayer();
    	if (event.getBlock() != null && event.getBlockReplacedState() != null && event.getBlockReplacedState().getTypeId() == 78) 
    	{
    			block = event.getBlockAgainst();
    		}
    		else 
    		{
    			block = event.getBlock();
    		}
    	if(player != null && m.shouldBeWatched(block))
    	{
    		if(block.getTypeId() != 17 && block.getTypeId() != 39 && block.getTypeId() != 40 && block.getTypeId() != 91 && block.getTypeId() != 86)
    			block.setData((byte) 5); //Change the byte
    		if(block.getTypeId() == 17 || block.getTypeId() == 39 || block.getTypeId() == 40 || block.getTypeId() == 91 || block.getTypeId() == 86)
    			plugin.misc.blockWatchList.add(block);
    	}
    	if(block.getTypeId() == 42 && LoadProperties.anvilmessages)
    	{
    		PlayerProfile PP = Users.getProfile(player);
    		if(LoadProperties.contribEnabled)
    		{
    			ContribPlayer cPlayer = ContribCraftPlayer.getContribPlayer(player);
	    		if(cPlayer.isBukkitContribEnabled())
	    		{
	    			if(!PP.getPlacedAnvil())
	    			{
	    				cPlayer.sendNotification("[mcMMO] Anvil Placed", "Right click to repair!", Material.IRON_BLOCK);
	    				PP.togglePlacedAnvil();
	    			}
	    		}
    		else
    		{
    			if(!PP.getPlacedAnvil())
    			{
    				event.getPlayer().sendMessage(mcLocale.getString("mcBlockListener.PlacedAnvil")); //$NON-NLS-1$
    				PP.togglePlacedAnvil();
    			}
    		}
    		}
    		else
    		{
    			if(!PP.getPlacedAnvil())
    			{
    				event.getPlayer().sendMessage(mcLocale.getString("mcBlockListener.PlacedAnvil")); //$NON-NLS-1$
    				PP.togglePlacedAnvil();
    			}
    		}
    	}
    }
    
    public void onBlockBreak(BlockBreakEvent event) 
    {
    	
    	
    	Player player = event.getPlayer();
    	PlayerProfile PP = Users.getProfile(player);
    	Block block = event.getBlock();
    	ItemStack inhand = player.getItemInHand();
    	if(event.isCancelled())
    		return;
    	if (event instanceof FakeBlockBreakEvent) 
    		return;
    	
   		/*
   		 * HERBALISM
   		 */
   		if(PP.getHoePreparationMode() && mcPermissions.getInstance().herbalismAbility(player) && block.getTypeId() == 59 && block.getData() == (byte) 0x07)
   		{
   			Herbalism.greenTerraCheck(player, block, plugin);
   		}
   		//Wheat && Triple drops
   		if(PP.getGreenTerraMode() && Herbalism.canBeGreenTerra(block))
   		{
   			Herbalism.herbalismProcCheck(block, player, event, plugin);
   			Herbalism.greenTerraWheat(player, block, event, plugin);
   		}
   		
   		
    	/*
    	 * MINING
    	 */
    	if(mcPermissions.getInstance().mining(player))
    	{
    		if(LoadProperties.miningrequirespickaxe)
    		{
    			if(m.isMiningPick(inhand))
    			{
    				if(PP.getSkillLevel(SkillType.MINING) >= 500)
    					Mining.miningBlockCheck(false, player, block, plugin);
    				else
    					Mining.miningBlockCheck(false, player, block, plugin);
    			}
    		} else 
    		{
    			if(PP.getSkillLevel(SkillType.MINING) >= 500)
					Mining.miningBlockCheck(false, player, block, plugin);
				else
					Mining.miningBlockCheck(false, player, block, plugin);
    		}
    	}
    	/*
   		 * WOOD CUTTING
   		 */
    	
   		if(player != null && block.getTypeId() == 17 && mcPermissions.getInstance().woodcutting(player))
   		{
   			if(LoadProperties.woodcuttingrequiresaxe)
   			{
				if(m.isAxes(inhand))
				{
					if(!plugin.misc.blockWatchList.contains(block))
					{
	    				WoodCutting.woodCuttingProcCheck(player, block);
	    				//Default
	    				if(block.getData() == (byte)0)
	    					PP.addXP(SkillType.WOODCUTTING, LoadProperties.mpine * LoadProperties.xpGainMultiplier);
	    				//Spruce
	    				if(block.getData() == (byte)1)
	    					PP.addXP(SkillType.WOODCUTTING, LoadProperties.mspruce * LoadProperties.xpGainMultiplier);
	    				//Birch
	    				if(block.getData() == (byte)2)
	    					PP.addXP(SkillType.WOODCUTTING, LoadProperties.mbirch * LoadProperties.xpGainMultiplier);
					}
    			}
    		} else 
    		{
    			if(!plugin.misc.blockWatchList.contains(block))
    			{
	    			WoodCutting.woodCuttingProcCheck(player, block);
	    			//Default
    				if(block.getData() == (byte)0)
    					PP.addXP(SkillType.WOODCUTTING, LoadProperties.mpine * LoadProperties.xpGainMultiplier);
    				//Spruce
    				if(block.getData() == (byte)1)
    					PP.addXP(SkillType.WOODCUTTING, LoadProperties.mspruce * LoadProperties.xpGainMultiplier);
    				//Birch
    				if(block.getData() == (byte)2)
    					PP.addXP(SkillType.WOODCUTTING, LoadProperties.mbirch * LoadProperties.xpGainMultiplier);
    			}
   			}
    		Skills.XpCheckSkill(SkillType.WOODCUTTING, player);
    			
    		/*
    		 * IF PLAYER IS USING TREEFELLER
    		 */
   			if(mcPermissions.getInstance().woodCuttingAbility(player) 
   					&& PP.getTreeFellerMode() 
   					&& block.getTypeId() == 17
   					&& m.blockBreakSimulate(block, player, plugin))
   			{
   				if(LoadProperties.contribEnabled)
   					contribStuff.playSoundForPlayer(SoundEffect.EXPLODE, player, block.getLocation());
   				
    			WoodCutting.treeFeller(block, player, plugin);
    			for(Block blockx : plugin.misc.treeFeller)
    			{
    				if(blockx != null){
    					Material mat = Material.getMaterial(block.getTypeId());
    					byte type = 0;
    					if(block.getTypeId() == 17)
    						type = block.getData();
    					ItemStack item = new ItemStack(mat, 1, (byte)0, type);
    					if(blockx.getTypeId() == 17){
    						blockx.getLocation().getWorld().dropItemNaturally(blockx.getLocation(), item);
    						//XP WOODCUTTING
    						if(!plugin.misc.blockWatchList.contains(block))
    						{
	    						WoodCutting.woodCuttingProcCheck(player, blockx);
	    						PP.addXP(SkillType.WOODCUTTING, LoadProperties.mpine);
    						}
    					}
    					if(blockx.getTypeId() == 18)
    					{
    						mat = Material.SAPLING;
    						
    						item = new ItemStack(mat, 1, (short)0, blockx.getData());
    						
    						if(Math.random() * 10 > 9)
    							blockx.getLocation().getWorld().dropItemNaturally(blockx.getLocation(), item);
    					}
    					if(blockx.getType() != Material.AIR)
    						player.incrementStatistic(Statistic.MINE_BLOCK, event.getBlock().getType());
    					blockx.setType(Material.AIR);
    				}
    			}
    			if(LoadProperties.toolsLoseDurabilityFromAbilities)
    		    	m.damageTool(player, (short) LoadProperties.abilityDurabilityLoss);
    			plugin.misc.treeFeller.clear();
    		}
    	}
    	/*
    	 * EXCAVATION
    	 */
    	if(mcPermissions.getInstance().excavation(player))
    		Excavation.excavationProcCheck(block.getData(), block.getTypeId(), block.getLocation(), player);
    	/*
    	 * HERBALISM
    	 */
    	if(PP.getHoePreparationMode() && mcPermissions.getInstance().herbalism(player) && Herbalism.canBeGreenTerra(block))
    	{
    		Herbalism.greenTerraCheck(player, block, plugin);
    	}
    	if(mcPermissions.getInstance().herbalism(player) && block.getData() != (byte) 5)
			Herbalism.herbalismProcCheck(block, player, event, plugin);
    	
    	//Change the byte back when broken
    	if(block.getData() == 5 && m.shouldBeWatched(block))
    	{
    		block.setData((byte) 0);
    		if(plugin.misc.blockWatchList.contains(block))
    		{
    			plugin.misc.blockWatchList.remove(block);
    		}
    	}
    	
    	
    }
    public void onBlockDamage(BlockDamageEvent event) 
    {
    	
    	
    	if(event.isCancelled())
    		return;
    	Player player = event.getPlayer();
    	PlayerProfile PP = Users.getProfile(player);
    	ItemStack inhand = player.getItemInHand();
    	Block block = event.getBlock();
    	
    	Skills.monitorSkills(player);

    	/*
    	 * ABILITY PREPARATION CHECKS
    	 */
   		if(PP.getHoePreparationMode() && Herbalism.canBeGreenTerra(block))
    		Herbalism.greenTerraCheck(player, block, plugin);
    	if(PP.getAxePreparationMode() && block.getTypeId() == 17)
    		WoodCutting.treeFellerCheck(player, block, plugin);
    	if(PP.getPickaxePreparationMode() && Mining.canBeSuperBroken(block))
    		Mining.superBreakerCheck(player, block, plugin);
    	if(PP.getShovelPreparationMode() && Excavation.canBeGigaDrillBroken(block))
    		Excavation.gigaDrillBreakerActivationCheck(player, block, plugin);
    	if(PP.getFistsPreparationMode() && (Excavation.canBeGigaDrillBroken(block) || block.getTypeId() == 78))
    		Unarmed.berserkActivationCheck(player, plugin);
    	
    	
    	/*
    	if(mcPermissions.getInstance().mining(player) && Mining.canBeSuperBroken(block) && 
    			m.blockBreakSimulate(block, player, plugin) && PP.getSkillLevel(SkillType.MINING) >= 250 
    			&& block.getType() != Material.STONE && m.isMiningPick(inhand))
    	{
    		contribStuff.playSoundForPlayer(SoundEffect.FIZZ, player, block.getLocation());
    		if(PP.getSkillLevel(SkillType.MINING) >= 500)
    		{
    			if(Math.random() * 100 > 99)
    			{
    				Mining.blockProcSmeltSimulate(block);
    				Mining.miningBlockCheck(true, player, block, plugin); //PROC
    				block.setType(Material.AIR);
    				contribStuff.playSoundForPlayer(SoundEffect.POP, player, block.getLocation());
    			}
    				
    		} else
    		{
    			if(Math.random() * 100 > 97)
    			{
    				Mining.blockProcSmeltSimulate(block);
    				Mining.miningBlockCheck(true, player, block, plugin); //PROC
    				block.setType(Material.AIR);
    				contribStuff.playSoundForPlayer(SoundEffect.POP, player, block.getLocation());
    			}
    		}
    	}
    	*/
    	
    	/*
    	 * TREE FELLAN STUFF
    	 */
    	if(LoadProperties.contribEnabled && block.getTypeId() == 17 && Users.getProfile(player).getTreeFellerMode())
    		contribStuff.playSoundForPlayer(SoundEffect.FIZZ, player, block.getLocation());
    	
    	/*
    	 * GREEN TERRA STUFF
    	 */
    	if(PP.getGreenTerraMode() && mcPermissions.getInstance().herbalismAbility(player) && PP.getGreenTerraMode()){
   			Herbalism.greenTerra(player, block);
   		}
    	
    	/*
    	 * GIGA DRILL BREAKER CHECKS
    	 */
    	if(PP.getGigaDrillBreakerMode() && m.blockBreakSimulate(block, player, plugin) 
    			&& Excavation.canBeGigaDrillBroken(block) && m.isShovel(inhand))
    	{
    		
    		int x = 1;
    		
    		while(x < 4)
    		{
    			Excavation.excavationProcCheck(block.getData(), block.getTypeId(), block.getLocation(), player);
    			x++;
    		}
    		
    		Material mat = Material.getMaterial(block.getTypeId());
    		if(block.getTypeId() == 2)
    			mat = Material.DIRT;
			byte type = block.getData();
			ItemStack item = new ItemStack(mat, 1, (byte)0, type);
			block.setType(Material.AIR);
			player.incrementStatistic(Statistic.MINE_BLOCK, event.getBlock().getType());
			if(LoadProperties.toolsLoseDurabilityFromAbilities)
	    		m.damageTool(player, (short) LoadProperties.abilityDurabilityLoss);
			block.getLocation().getWorld().dropItemNaturally(block.getLocation(), item);
			
			//Contrib stuff
			if(LoadProperties.contribEnabled)
				contribStuff.playSoundForPlayer(SoundEffect.POP, player, block.getLocation());
    	}
    	/*
    	 * BERSERK MODE CHECKS
    	 */
    	if(PP.getBerserkMode() 
    		&& m.blockBreakSimulate(block, player, plugin) 
    		&& player.getItemInHand().getTypeId() == 0 
    		&& (Excavation.canBeGigaDrillBroken(block) || block.getTypeId() == 78))
    	{
		   	Material mat = Material.getMaterial(block.getTypeId());
		   	if(block.getTypeId() == 2)
		   		mat = Material.DIRT;
		   	if(block.getTypeId() == 78)
		   		mat = Material.SNOW_BALL;
			byte type = block.getData();
			ItemStack item = new ItemStack(mat, 1, (byte)0, type);
			player.incrementStatistic(Statistic.MINE_BLOCK, event.getBlock().getType());
			block.setType(Material.AIR);
			block.getLocation().getWorld().dropItemNaturally(block.getLocation(), item);
			
			if(LoadProperties.contribEnabled)
				contribStuff.playSoundForPlayer(SoundEffect.POP, player, block.getLocation());
    	}
    	
    	/*
    	 * SUPER BREAKER CHECKS
    	 */
    	if(PP.getSuperBreakerMode() 
    			&& Mining.canBeSuperBroken(block)
    			&& m.blockBreakSimulate(block, player, plugin))
    	{
    		
    		if(LoadProperties.miningrequirespickaxe)
    		{
    			if(m.isMiningPick(inhand))
    				Mining.SuperBreakerBlockCheck(player, block, plugin);
    		} else {
    			Mining.SuperBreakerBlockCheck(player, block, plugin);
    		}
    	}
    	
    	/*
    	 * LEAF BLOWER
    	 */
    	if(block.getTypeId() == 18 && mcPermissions.getInstance().woodcutting(player) && PP.getSkillLevel(SkillType.WOODCUTTING) >= 100 && m.isAxes(player.getItemInHand()) && m.blockBreakSimulate(block, player, plugin))
    	{
    		m.damageTool(player, (short)1);
    		if(Math.random() * 10 > 9)
    		{
    			ItemStack x = new ItemStack(Material.SAPLING, 1, (short)0, block.getData());
    			block.getLocation().getWorld().dropItemNaturally(block.getLocation(), x);
    		}
    		block.setType(Material.AIR);
    		player.incrementStatistic(Statistic.MINE_BLOCK, event.getBlock().getType());
    		if(LoadProperties.contribEnabled)
    			contribStuff.playSoundForPlayer(SoundEffect.POP, player, block.getLocation());
    	}
    	if(block.getType() == Material.AIR && plugin.misc.blockWatchList.contains(block))
    	{
    		plugin.misc.blockWatchList.remove(block);
    	}
    }
    
    public void onBlockFromTo(BlockFromToEvent event) 
    {
    	
    	
        Block blockFrom = event.getBlock();
        Block blockTo = event.getToBlock();
        if(m.shouldBeWatched(blockFrom) && blockFrom.getData() == (byte)5)
        {
        	blockTo.setData((byte)5);
        }
    }
}