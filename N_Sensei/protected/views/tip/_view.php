<?php
/* @var $this TipController */
/* @var $data Tip */
?>

<div class="view">

	<b><?php echo CHtml::encode($data->getAttributeLabel('ID')); ?>:</b>
	<?php echo CHtml::link(CHtml::encode($data->ID), array('view', 'id'=>$data->ID)); ?>
	<br />

	<b><?php echo CHtml::encode($data->getAttributeLabel('NAZIV')); ?>:</b>
	<?php echo CHtml::encode($data->NAZIV); ?>
	<br />

	<b><?php echo CHtml::encode($data->getAttributeLabel('EM')); ?>:</b>
	<?php echo CHtml::encode($data->EM); ?>
	<br />

	<b><?php echo CHtml::encode($data->getAttributeLabel('PROTOKOL')); ?>:</b>
	<?php echo CHtml::encode($data->PROTOKOL); ?>
	<br />

	<b><?php echo CHtml::encode($data->getAttributeLabel('DATA')); ?>:</b>
	<?php echo CHtml::encode($data->DATA); ?>
	<br />


</div>