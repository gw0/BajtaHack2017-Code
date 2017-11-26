<?php
/* @var $this SenzorController */
/* @var $data Senzor */
?>

<div class="view">

	<b><?php echo CHtml::encode($data->getAttributeLabel('ID')); ?>:</b>
	<?php echo CHtml::link(CHtml::encode($data->ID), array('view', 'id'=>$data->ID)); ?>
	<br />

	<b><?php echo CHtml::encode($data->getAttributeLabel('NAZIV')); ?>:</b>
	<?php echo CHtml::encode($data->NAZIV); ?>
	<br />

	<b><?php echo CHtml::encode($data->getAttributeLabel('NAPRAVA_ID')); ?>:</b>
	<?php echo CHtml::encode($data->NAPRAVA_ID); ?>
	<br />

	<b><?php echo CHtml::encode($data->getAttributeLabel('TIP_ID')); ?>:</b>
	<?php echo CHtml::encode($data->TIP_ID); ?>
	<br />

	<b><?php echo CHtml::encode($data->getAttributeLabel('NASLOV')); ?>:</b>
	<?php echo CHtml::encode($data->NASLOV); ?>
	<br />


</div>