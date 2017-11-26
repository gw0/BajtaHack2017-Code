<?php
/* @var $this NapravaController */
/* @var $data Naprava */
?>

<div class="view">

	<b><?php echo CHtml::encode($data->getAttributeLabel('ID')); ?>:</b>
	<?php echo CHtml::link(CHtml::encode($data->ID), array('view', 'id'=>$data->ID)); ?>
	<br />

	<b><?php echo CHtml::encode($data->getAttributeLabel('NAZIV')); ?>:</b>
	<?php echo CHtml::encode($data->NAZIV); ?>
	<br />

	<b><?php echo CHtml::encode($data->getAttributeLabel('NASLOV')); ?>:</b>
	<?php echo CHtml::encode($data->NASLOV); ?>
	<br />


</div>