package com.ninetyseconds.auckland.lastadapter

import com.github.nitrico.lastadapter.Type
import com.ninetyseconds.auckland.R
import com.ninetyseconds.auckland.databinding.ItemAssetGroupBinding
import com.ninetyseconds.auckland.databinding.ItemBrandBinding
import com.ninetyseconds.auckland.databinding.ItemCategoryBinding
import com.ninetyseconds.auckland.databinding.ItemCreatorProgressBinding
import com.ninetyseconds.auckland.databinding.ItemInviteBinding
import com.ninetyseconds.auckland.databinding.ItemMapStaticWithAddressBinding
import com.ninetyseconds.auckland.databinding.ItemNameDescrIconBinding
import com.ninetyseconds.auckland.databinding.ItemNoLocationBinding
import com.ninetyseconds.auckland.databinding.ItemOpportunityBinding
import com.ninetyseconds.auckland.databinding.ItemOpportunityRoleBinding
import com.ninetyseconds.auckland.databinding.ItemProjectProgressItemBinding
import com.ninetyseconds.auckland.databinding.ItemProjectProgressStateBinding
import com.ninetyseconds.auckland.databinding.ItemProjectResourceBinding
import com.ninetyseconds.auckland.databinding.ItemProjectResourceConfigurationBinding
import com.ninetyseconds.auckland.databinding.ItemProjectTaskHeaderBinding
import com.ninetyseconds.auckland.databinding.ItemProjectTeamTasksBinding
import com.ninetyseconds.auckland.databinding.ItemTitleBinding
import com.ninetyseconds.auckland.databinding.ItemTitleDetailedBinding
import com.ninetyseconds.auckland.databinding.ItemTodoBinding

val todoItemType = Type<ItemTodoBinding>(R.layout.item_todo)
val userProgressType = Type<ItemCreatorProgressBinding>(R.layout.item_creator_progress)
val titleItemType = Type<ItemTitleBinding>(R.layout.item_title)
val titleDetailedItemType = Type<ItemTitleDetailedBinding>(R.layout.item_title_detailed)
val categoryItemType = Type<ItemCategoryBinding>(R.layout.item_category)
val opportunityItemType = Type<ItemOpportunityBinding>(R.layout.item_opportunity)

val inviteItemType = Type<ItemInviteBinding>(R.layout.item_invite)
val brandItemType = Type<ItemBrandBinding>(R.layout.item_brand)
val assetGroupItemType = Type<ItemAssetGroupBinding>(R.layout.item_asset_group)
val projectResourceType = Type<ItemProjectResourceBinding>(R.layout.item_project_resource)

val progressItemType = Type<ItemProjectProgressItemBinding>(R.layout.item_project_progress_item)
val configureItemType = Type<ItemProjectResourceConfigurationBinding>(R.layout.item_project_resource_configuration)
val taskHeaderItemType = Type<ItemProjectTaskHeaderBinding>(R.layout.item_project_task_header)
val projectTeamTasksItemType = Type<ItemProjectTeamTasksBinding>(R.layout.item_project_team_tasks)

val opportunityRoleItemType = Type<ItemOpportunityRoleBinding>(R.layout.item_opportunity_role)
val opportunityMapItemType = Type<ItemMapStaticWithAddressBinding>(R.layout.item_map_static_with_address)
val opportunityEmptyMapItemType = Type<ItemNoLocationBinding>(R.layout.item_no_location)
val opportunityTaskCommonItemType = Type<ItemNameDescrIconBinding>(R.layout.item_name_descr_icon)
val opportunityTaskTitleItemType = Type<ItemProjectProgressStateBinding>(R.layout.item_project_progress_state)
val opportunityTaskItemType = Type<ItemProjectProgressItemBinding>(R.layout.item_project_progress_item)
