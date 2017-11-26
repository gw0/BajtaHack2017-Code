<?php
/* @var $this MeritevController */
/* @var $data Meritev */
?>

<div class="view">

	<b><?php echo CHtml::encode($data->getAttributeLabel('ID')); ?>:</b>
	<?php echo CHtml::link(CHtml::encode($data->ID), array('view', 'id'=>$data->ID)); ?>
	<br />

	<b><?php echo CHtml::encode($data->getAttributeLabel('SENZOR_ID')); ?>:</b>
	<?php echo CHtml::encode($data->SENZOR_ID); ?>
	<br />

	<b><?php echo CHtml::encode($data->getAttributeLabel('VALUE')); ?>:</b>
	<?php echo CHtml::encode($data->VALUE); ?>
	<br />

	<b><?php echo CHtml::encode($data->getAttributeLabel('CREATED')); ?>:</b>
	<?php echo CHtml::encode($data->CREATED); ?>
	<br />


</div>