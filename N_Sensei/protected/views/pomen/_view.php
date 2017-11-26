<?php
/* @var $this PomenController */
/* @var $data Pomen */
?>

<div class="view">

	<b><?php echo CHtml::encode($data->getAttributeLabel('ID')); ?>:</b>
	<?php echo CHtml::link(CHtml::encode($data->ID), array('view', 'id'=>$data->ID)); ?>
	<br />

	<b><?php echo CHtml::encode($data->getAttributeLabel('SENZOR_ID')); ?>:</b>
	<?php echo CHtml::encode($data->SENZOR_ID); ?>
	<br />

	<b><?php echo CHtml::encode($data->getAttributeLabel('POMEN')); ?>:</b>
	<?php echo CHtml::encode($data->POMEN); ?>
	<br />

	<b><?php echo CHtml::encode($data->getAttributeLabel('MIN_VREDNOST')); ?>:</b>
	<?php echo CHtml::encode($data->MIN_VREDNOST); ?>
	<br />

	<b><?php echo CHtml::encode($data->getAttributeLabel('MAX_VREDNOST')); ?>:</b>
	<?php echo CHtml::encode($data->MAX_VREDNOST); ?>
	<br />


</div>