<?php
/* @var $this SenzorController */
/* @var $model Senzor */
/* @var $form CActiveForm */
?>

<div class="form">

<?php $form=$this->beginWidget('CActiveForm', array(
	'id'=>'senzor-form',
	// Please note: When you enable ajax validation, make sure the corresponding
	// controller action is handling ajax validation correctly.
	// There is a call to performAjaxValidation() commented in generated controller code.
	// See class documentation of CActiveForm for details on this.
	'enableAjaxValidation'=>false,
)); ?>

	

	<?php echo $form->errorSummary($model); ?>

	<div class="row">
		<?php echo $form->labelEx($model,'NAZIV'); ?>
		<?php echo $form->textField($model,'NAZIV',array('size'=>60,'maxlength'=>300)); ?>
		<?php echo $form->error($model,'NAZIV'); ?>
	</div>

	<div class="row">
		<?php echo $form->labelEx($model,'NAPRAVA_ID'); ?>
		<?php echo $form->dropDownList($model, 'NAPRAVA_ID', CHtml::listData(Naprava::model()->findAll(), 'ID', 'NAZIV')); ?>
		<?php echo $form->error($model,'NAPRAVA_ID'); ?>
	</div>

	<div class="row">
		<?php echo $form->labelEx($model,'TIP_ID'); ?>
		<?php echo $form->dropDownList($model, 'TIP_ID', CHtml::listData(Tip::model()->findAll(), 'ID', 'NAZIV')); ?>
		<?php echo $form->error($model,'TIP_ID'); ?>
	</div>

	<div class="row">
		<?php echo $form->labelEx($model,'NASLOV'); ?>
		<?php echo $form->textField($model,'NASLOV'); ?>
		<?php echo $form->error($model,'NASLOV'); ?>
	</div>

	<div class="row buttons">
		<?php echo CHtml::submitButton($model->isNewRecord ? 'Kreiraj' : 'Save'); ?>
	</div>

<?php $this->endWidget(); ?>

</div><!-- form -->